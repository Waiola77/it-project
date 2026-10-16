package org.example.consultant.service;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.example.consultant.aiservices.IntentExtractionService;
import org.example.consultant.model.Book;
import org.example.consultant.model.SearchIntent;
import org.springframework.stereotype.Service;
import org.example.consultant.model.BookRecommendation;
import org.example.consultant.model.RecommendationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookChatService {

    private static final int CANDIDATE_LIMIT = 10;

    private final BookRetrievalService retrievalService;
    private final BookRecommendationServices aiService;
    private final IntentExtractionService intentExtractionService;
    private final BookNormalizationService normalizationService;
    private final UserFeedbackService feedbackService;
    private final UserPreferenceService preferenceService;
    private final DemoUserProfileService demoUserProfileService;

    public BookChatService(BookRetrievalService retrievalService,
                           BookRecommendationServices aiService,
                           IntentExtractionService intentExtractionService,
                           BookNormalizationService normalizationService,
                           UserFeedbackService feedbackService,
                           UserPreferenceService preferenceService,
                           DemoUserProfileService demoUserProfileService) {
        this.retrievalService = retrievalService;
        this.aiService = aiService;
        this.intentExtractionService = intentExtractionService;
        this.normalizationService = normalizationService;
        this.feedbackService = feedbackService;
        this.preferenceService = preferenceService;
        this.demoUserProfileService = demoUserProfileService;
    }

    public RecommendationResponse chat(String userId, String userMessage) {

        SearchIntent intent = intentExtractionService.extractIntent(userMessage);

        List<Book> similarBooks;

        boolean hasExactTarget =
                (intent.getTitle() != null && !intent.getTitle().isBlank())
                        || (intent.getAuthor() != null && !intent.getAuthor().isBlank());

        if (hasExactTarget) {
            String normalizedTitle =
                    normalizationService.normalizeTitle(intent.getTitle());
            String normalizedAuthor =
                    normalizationService.normalizeAuthor(intent.getAuthor());

            similarBooks =
                    retrievalService.findByTitleOrAuthor(
                            normalizedTitle,
                            normalizedAuthor
                    );
        } else {
            String queryText =
                    (intent.getSemanticQuery() != null
                            && !intent.getSemanticQuery().isBlank())
                            ? intent.getSemanticQuery()
                            : userMessage;

            similarBooks =
                    retrievalService.findSimilarBooksByText(
                            queryText,
                            CANDIDATE_LIMIT
                    );
        }

        List<String> excludedBookIds = new java.util.ArrayList<>();

        // Exclude books rejected through live user feedback
        excludedBookIds.addAll(
                feedbackService.getRejectedBooks(userId)
                        .stream()
                        .map(feedback -> feedback.getBookId())
                        .toList()
        );

        // For demo users, also exclude books already read or previously rejected
        demoUserProfileService.getProfileById(userId).ifPresent(profile -> {

            if (profile.getReadingHistoryRecordIds() != null) {
                excludedBookIds.addAll(profile.getReadingHistoryRecordIds());
            }

            if (profile.getRejectedRecordIds() != null) {
                excludedBookIds.addAll(profile.getRejectedRecordIds());
            }
        });

        List<Book> filteredBooks =
                similarBooks.stream()
                        .filter(book ->
                                !excludedBookIds.contains(book.getRecordId()))
                        .toList();

        String context = filteredBooks.stream()
                .map(b -> "- Record ID: %s  Title: %s  Author: %s  Description: %s".formatted(
                        b.getRecordId(),
                        b.getTitle(),
                        String.join(", ", b.getAuthorsRaw()),
                        b.getDescription() == null ? "" :
                                (b.getDescription().length() > 250
                                        ? b.getDescription().substring(0, 250) + "..."
                                        : b.getDescription())))
                .collect(Collectors.joining("\n"));

        if (context.isBlank()) {
            context = "No relevant books were found.\n";
        }

        String preferenceContext =
                preferenceService.buildPreferenceContext(userId);

        RecommendationResponse response = aiService.chat(
                userMessage,
                context,
                preferenceContext
        );


        List<BookRecommendation> finalRecommendations =
                response.getRecommendations().stream()

                        // Remove books that clearly conflict with the user's
                        // explicit negative preferences
                        .filter(recommendation ->
                                !recommendation.isNegativeConflict()
                        )

                        // Only accept books that actually came from this retrieval
                        .filter(recommendation ->
                                filteredBooks.stream().anyMatch(book ->
                                        book.getRecordId().equals(recommendation.getRecordId())
                                )
                        )

                        // Strong matches first, then moderate, then weak
                        .sorted(java.util.Comparator.comparingInt(recommendation ->
                                switch (recommendation.getMatchStrength() == null
                                        ? ""
                                        : recommendation.getMatchStrength().toLowerCase()) {
                                    case "strong" -> 0;
                                    case "moderate" -> 1;
                                    case "weak" -> 2;
                                    default -> 3;
                                }
                        ))

                        .limit(10)
                        .collect(Collectors.toList());

        for (int i = 0; i < finalRecommendations.size(); i++) {

            BookRecommendation recommendation = finalRecommendations.get(i);

            Book matchedBook = filteredBooks.stream()
                    .filter(book ->
                            book.getRecordId().equals(recommendation.getRecordId()))
                    .findFirst()
                    .orElse(null);

            if (matchedBook != null) {
                recommendation.setAuthor(
                        String.join(", ", matchedBook.getAuthorsRaw())
                );
                recommendation.setDescription(
                        matchedBook.getDescription()
                );
            }

            recommendation.setRank(i + 1);
        }

        response.setRecommendations(finalRecommendations);

        return response;
    }
}