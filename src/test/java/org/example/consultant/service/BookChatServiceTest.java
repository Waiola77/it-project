package org.example.consultant.service;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.example.consultant.aiservices.IntentExtractionService;
import org.example.consultant.model.Book;
import org.example.consultant.model.BookRecommendation;
import org.example.consultant.model.DemoUserProfile;
import org.example.consultant.model.RecommendationResponse;
import org.example.consultant.model.SearchIntent;
import org.example.consultant.model.UserFeedback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookChatServiceTest {

    @Mock
    private BookRetrievalService retrievalService;

    @Mock
    private BookRecommendationServices aiService;

    @Mock
    private IntentExtractionService intentExtractionService;

    @Mock
    private BookNormalizationService normalizationService;

    @Mock
    private UserFeedbackService feedbackService;

    @Mock
    private UserPreferenceService preferenceService;

    @Spy
    private DemoUserProfileService demoUserProfileService = new DemoUserProfileService();

    @InjectMocks
    private BookChatService bookChatService;

    @Test
    void shouldUseExactTitleOrAuthorSearchWhenIntentContainsSpecificTarget() {
        String userId = "science-reader";
        String message = "Can you recommend Pride and Prejudice by Jane Austen?";
        SearchIntent intent =
                new SearchIntent("classic romance", "Pride and Prejudice", "Jane Austen", null);
        Book book =
                book("book-1", "Pride and Prejudice", "Jane Austen", "A classic romantic novel.");
        RecommendationResponse aiResponse =
                new RecommendationResponse(List.of(
                        new BookRecommendation("book-1", "Pride and Prejudice", "Relevant classic", 1)
                ));

        when(intentExtractionService.extractIntent(message)).thenReturn(intent);
        when(normalizationService.normalizeTitle("Pride and Prejudice"))
                .thenReturn("pride and prejudice");
        when(normalizationService.normalizeAuthor("Jane Austen")).thenReturn("jane austen");
        when(retrievalService.findByTitleOrAuthor("pride and prejudice", "jane austen"))
                .thenReturn(List.of(book));
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext(userId))
                .thenReturn("No previous user preference history is available.");
        when(aiService.chat(anyString(), anyString(), anyString())).thenReturn(aiResponse);

        RecommendationResponse response = bookChatService.chat(userId, message);

        verify(retrievalService).findByTitleOrAuthor("pride and prejudice", "jane austen");
        verify(retrievalService, never()).findSimilarBooksByText(anyString(), anyInt());
        assertEquals("Jane Austen", response.getRecommendations().get(0).getAuthor());
        assertEquals("A classic romantic novel.", response.getRecommendations().get(0).getDescription());
    }

    @Test
    void shouldUseSemanticQueryWhenIntentHasNoSpecificTitleOrAuthor() {
        String userId = "new-gentle-fiction-reader";
        String message = "I want something warm about friendship.";
        SearchIntent intent = new SearchIntent("warm friendship fiction", null, null, null);

        when(intentExtractionService.extractIntent(message)).thenReturn(intent);
        when(retrievalService.findSimilarBooksByText("warm friendship fiction", 10))
                .thenReturn(List.of(book("book-2", "Gentle Days", "A Writer", "A story about friendship.")));
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("likes warm stories");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of(
                        new BookRecommendation("book-2", "Gentle Days", "Matches friendship request", 1)
                )));

        bookChatService.chat(userId, message);

        verify(retrievalService).findSimilarBooksByText("warm friendship fiction", 10);
        verify(retrievalService, never()).findByTitleOrAuthor(anyString(), anyString());
    }

    @ParameterizedTest(name = "Candidate filtering for {0}")
    @ValueSource(strings = {"romance-reader", "science-reader", "self-improvement-reader",
            "mystery-reader", "history-adventure-reader", "new-gentle-fiction-reader"})
    void shouldExcludeActualDemoReadingHistoryAndRejectedBooks(String userId) {
        DemoUserProfile profile = demoUserProfileService.getProfileById(userId).orElseThrow();
        String message = "Recommend me a book.";
        // A saved book is an unread interest signal, so it should remain eligible.
        String allowedId = profile.getSavedBookRecordIds().get(0);
        Book allowedBook = book(allowedId, "Saved catalogue fixture", "Author", "An unread saved book.");
        List<String> excludedIds = new java.util.ArrayList<>(profile.getReadingHistoryRecordIds());
        excludedIds.addAll(profile.getRejectedRecordIds());
        excludedIds.add("live-rejected");
        List<Book> candidates = new java.util.ArrayList<>();
        candidates.add(allowedBook);
        for (String id : excludedIds) {
            candidates.add(book(id, "Excluded catalogue fixture " + id, "Author", "Should be filtered."));
        }

        when(intentExtractionService.extractIntent(message))
                .thenReturn(new SearchIntent("book request", null, null, null));
        when(retrievalService.findSimilarBooksByText("book request", 10)).thenReturn(candidates);
        when(feedbackService.getRejectedBooks(userId))
                .thenReturn(List.of(new UserFeedback(userId, "live-rejected", "REJECTED")));
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("profile preferences");
        // Include excluded IDs in the mocked AI output too, to verify the final safeguard.
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(candidates.stream()
                        .map(b -> recommendation(b.getRecordId(), "strong", false)).toList()));

        RecommendationResponse response = bookChatService.chat(userId, message);

        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiService).chat(eq(message), contextCaptor.capture(), eq("profile preferences"));
        String context = contextCaptor.getValue();
        assertTrue(context.contains("Record ID: " + allowedId + "  Title:"));
        for (String id : excludedIds) {
            assertFalse(context.contains("Record ID: " + id + "  Title:"));
        }
        assertEquals(List.of(allowedId), ids(response));
    }

    @Test
    void shouldFallBackToOriginalMessageWhenSemanticQueryIsBlank() {
        String userId = "new-reader";
        String message = "Recommend me a book.";
        SearchIntent intent = new SearchIntent("   ", null, null, null);

        when(intentExtractionService.extractIntent(message)).thenReturn(intent);
        when(retrievalService.findSimilarBooksByText(message, 10))
                .thenReturn(List.of(book("book-3", "A Book", "Another Writer", "A useful description.")));
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of(
                        new BookRecommendation("book-3", "A Book", "Relevant", 1)
                )));

        bookChatService.chat(userId, message);

        verify(retrievalService).findSimilarBooksByText(message, 10);
    }

    @Test
    void shouldTellAiServiceWhenNoCandidateBooksAreFound() {
        String userId = "specific-reader";
        String message = "Find me something very specific.";

        when(intentExtractionService.extractIntent(message))
                .thenReturn(new SearchIntent("specific request", null, null, null));
        when(retrievalService.findSimilarBooksByText("specific request", 10)).thenReturn(List.of());
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of()));

        RecommendationResponse response = bookChatService.chat(userId, message);

        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiService).chat(eq(message), contextCaptor.capture(), eq("no history"));
        assertTrue(contextCaptor.getValue().contains("No relevant books were found."));
        assertTrue(response.getRecommendations().isEmpty());
    }

    @Test
    void shouldRemoveNegativeConflictsEvenWhenTheyAreStrongMatches() {
        Book conflict = book("conflict", "Violent Mystery", "Author", "Contains violence.");
        Book safe = book("safe", "Gentle Mystery", "Author", "A gentle puzzle.");
        stubSemanticChat(List.of(conflict, safe), List.of(
                recommendation("conflict", "strong", true),
                recommendation("safe", "moderate", false)));

        RecommendationResponse response = bookChatService.chat("reader", "A mystery without violence");

        assertEquals(List.of("safe"), ids(response));
        assertEquals(1, response.getRecommendations().get(0).getRank());
    }

    @Test
    void shouldRejectAiBooksOutsideRetrievedCatalogueCandidates() {
        stubSemanticChat(List.of(book("real", "Real Book", "Author", "Description")), List.of(
                recommendation("invented", "strong", false),
                recommendation("real", "moderate", false)));

        assertEquals(List.of("real"), ids(bookChatService.chat("reader", "Find a book")));
    }

    @Test
    void shouldRankByMatchStrengthAndPreserveAiOrderWithinEachGroup() {
        List<String> bookIds = List.of("weak", "strong-1", "moderate", "strong-2");
        List<Book> candidates = bookIds.stream()
                .map(id -> book(id, id, "Author", "Description")).toList();
        stubSemanticChat(candidates, List.of(
                recommendation("weak", "weak", false),
                recommendation("strong-1", "strong", false),
                recommendation("moderate", "moderate", false),
                recommendation("strong-2", "strong", false)));

        RecommendationResponse response = bookChatService.chat("reader", "Find a book");

        assertEquals(List.of("strong-1", "strong-2", "moderate", "weak"), ids(response));
        assertEquals(List.of(1, 2, 3, 4), response.getRecommendations().stream()
                .map(BookRecommendation::getRank).toList());
    }

    @Test
    void shouldLimitExactSearchResultsToTenAfterFilteringAndRanking() {
        List<Book> candidates = java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(i -> book("book-" + i, "Book " + i, "Author", "Description")).toList();
        List<BookRecommendation> ranked = candidates.stream()
                .map(b -> recommendation(b.getRecordId(), "moderate", false)).toList();
        // Put the strongest match beyond the first ten to catch limiting before ranking.
        ranked.get(11).setMatchStrength("strong");
        ranked.get(0).setNegativeConflict(true);
        when(intentExtractionService.extractIntent("Books by Author"))
                .thenReturn(new SearchIntent(null, null, "Author", null));
        when(normalizationService.normalizeAuthor("Author")).thenReturn("author");
        when(retrievalService.findByTitleOrAuthor(null, "author")).thenReturn(candidates);
        when(feedbackService.getRejectedBooks("reader")).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext("reader")).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(ranked));

        RecommendationResponse response = bookChatService.chat("reader", "Books by Author");

        assertEquals(10, response.getRecommendations().size());
        assertEquals(List.of("book-12", "book-2", "book-3", "book-4", "book-5",
                "book-6", "book-7", "book-8", "book-9", "book-10"), ids(response));
        assertEquals(java.util.stream.IntStream.rangeClosed(1, 10).boxed().toList(),
                response.getRecommendations().stream().map(BookRecommendation::getRank).toList());
    }

    @ParameterizedTest
    @ValueSource(ints = {250, 251})
    void shouldBoundAiDescriptionWithoutTruncatingReturnedCatalogueDetails(int length) {
        String description = "x".repeat(length);
        Book candidate = book("one", "One", "Catalogue Author", description);
        BookRecommendation aiBook = recommendation("one", "strong", false);
        aiBook.setAuthor("Incorrect AI author");
        aiBook.setDescription("Incorrect AI description");
        stubSemanticChat(List.of(candidate), List.of(aiBook));

        RecommendationResponse response = bookChatService.chat("reader", "Find a book");

        ArgumentCaptor<String> context = ArgumentCaptor.forClass(String.class);
        verify(aiService).chat(eq("Find a book"), context.capture(), eq("no history"));
        String expectedDescription = length > 250 ? "x".repeat(250) + "..." : description;
        assertEquals("- Record ID: one  Title: One  Author: Catalogue Author  Description: "
                + expectedDescription, context.getValue());
        assertEquals(description, response.getRecommendations().get(0).getDescription());
        assertEquals("Catalogue Author", response.getRecommendations().get(0).getAuthor());
    }

    private void stubSemanticChat(List<Book> candidates, List<BookRecommendation> recommendations) {
        when(intentExtractionService.extractIntent(anyString()))
                .thenReturn(new SearchIntent("query", null, null, null));
        when(retrievalService.findSimilarBooksByText("query", 10)).thenReturn(candidates);
        when(feedbackService.getRejectedBooks("reader")).thenReturn(List.of());
        when(preferenceService.buildPreferenceContext("reader")).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(recommendations));
    }

    private BookRecommendation recommendation(String id, String strength, boolean conflict) {
        BookRecommendation recommendation = new BookRecommendation(id, id, "Reason", 99);
        recommendation.setMatchStrength(strength);
        recommendation.setNegativeConflict(conflict);
        return recommendation;
    }

    private List<String> ids(RecommendationResponse response) {
        return response.getRecommendations().stream().map(BookRecommendation::getRecordId).toList();
    }

    private Book book(String recordId, String title, String author, String description) {
        return new Book(
                recordId,
                title,
                List.of(author),
                description,
                "VA",
                title.toLowerCase(),
                List.of(author.toLowerCase()),
                true
        );
    }
}
