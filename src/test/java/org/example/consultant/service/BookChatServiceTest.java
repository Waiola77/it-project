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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    @Mock
    private DemoUserProfileService demoUserProfileService;

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
        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.empty());
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
        String userId = "gentle-reader";
        String message = "I want something warm about friendship.";
        SearchIntent intent = new SearchIntent("warm friendship fiction", null, null, null);

        when(intentExtractionService.extractIntent(message)).thenReturn(intent);
        when(retrievalService.findSimilarBooksByText("warm friendship fiction", 20))
                .thenReturn(List.of(book("book-2", "Gentle Days", "A Writer", "A story about friendship.")));
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.empty());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("likes warm stories");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of(
                        new BookRecommendation("book-2", "Gentle Days", "Matches friendship request", 1)
                )));

        bookChatService.chat(userId, message);

        verify(retrievalService).findSimilarBooksByText("warm friendship fiction", 20);
        verify(retrievalService, never()).findByTitleOrAuthor(anyString(), anyString());
    }

    @Test
    void shouldExcludeRejectedAndPreviouslyReadDemoBooksFromAiContext() {
        String userId = "romance-reader";
        String message = "I want a romantic story.";
        Book allowedBook = book("allowed-1", "New Romance", "Author One", "A warm relationship story.");
        Book liveRejectedBook = book("rejected-live", "Rejected Live", "Author Two", "Should be filtered.");
        Book alreadyReadBook = book("read-before", "Already Read", "Author Three", "Should be filtered.");
        Book profileRejectedBook =
                book("rejected-profile", "Rejected Profile", "Author Four", "Should be filtered.");
        DemoUserProfile profile = new DemoUserProfile();
        profile.setReadingHistoryRecordIds(List.of("read-before"));
        profile.setRejectedRecordIds(List.of("rejected-profile"));

        when(intentExtractionService.extractIntent(message))
                .thenReturn(new SearchIntent("romantic story", null, null, null));
        when(retrievalService.findSimilarBooksByText("romantic story", 20))
                .thenReturn(List.of(allowedBook, liveRejectedBook, alreadyReadBook, profileRejectedBook));
        when(feedbackService.getRejectedBooks(userId))
                .thenReturn(List.of(new UserFeedback(userId, "rejected-live", "REJECTED")));
        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.of(profile));
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("profile preferences");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of(
                        new BookRecommendation("allowed-1", "New Romance", "Relevant", 1)
                )));

        bookChatService.chat(userId, message);

        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiService).chat(eq(message), contextCaptor.capture(), eq("profile preferences"));
        String context = contextCaptor.getValue();
        assertTrue(context.contains("allowed-1"));
        assertFalse(context.contains("rejected-live"));
        assertFalse(context.contains("read-before"));
        assertFalse(context.contains("rejected-profile"));
    }

    @Test
    void shouldFallBackToOriginalMessageWhenSemanticQueryIsBlank() {
        String userId = "new-reader";
        String message = "Recommend me a book.";
        SearchIntent intent = new SearchIntent("   ", null, null, null);

        when(intentExtractionService.extractIntent(message)).thenReturn(intent);
        when(retrievalService.findSimilarBooksByText(message, 20))
                .thenReturn(List.of(book("book-3", "A Book", "Another Writer", "A useful description.")));
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.empty());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of(
                        new BookRecommendation("book-3", "A Book", "Relevant", 1)
                )));

        bookChatService.chat(userId, message);

        verify(retrievalService).findSimilarBooksByText(message, 20);
    }

    @Test
    void shouldTellAiServiceWhenNoCandidateBooksAreFound() {
        String userId = "specific-reader";
        String message = "Find me something very specific.";

        when(intentExtractionService.extractIntent(message))
                .thenReturn(new SearchIntent("specific request", null, null, null));
        when(retrievalService.findSimilarBooksByText("specific request", 20)).thenReturn(List.of());
        when(feedbackService.getRejectedBooks(userId)).thenReturn(List.of());
        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.empty());
        when(preferenceService.buildPreferenceContext(userId)).thenReturn("no history");
        when(aiService.chat(anyString(), anyString(), anyString()))
                .thenReturn(new RecommendationResponse(List.of()));

        bookChatService.chat(userId, message);

        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiService).chat(eq(message), contextCaptor.capture(), eq("no history"));
        assertTrue(contextCaptor.getValue().contains("No relevant books were found."));
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
