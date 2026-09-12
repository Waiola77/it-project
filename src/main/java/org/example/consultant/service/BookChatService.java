package org.example.consultant.service;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.example.consultant.model.Book;
import org.springframework.stereotype.Service;
import org.example.consultant.model.BookRecommendation;
import org.example.consultant.model.RecommendationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookChatService {

    private static final int CANDIDATE_LIMIT = 20;

    private final BookRetrievalService retrievalService;
    private final BookRecommendationServices aiService;
    private final UserFeedbackService feedbackService;
    private final UserPreferenceService preferenceService;

    public BookChatService(BookRetrievalService retrievalService,
                           BookRecommendationServices aiService,
                           UserFeedbackService feedbackService,
                           UserPreferenceService preferenceService) {
        this.retrievalService = retrievalService;
        this.aiService = aiService;
        this.feedbackService = feedbackService;
        this.preferenceService = preferenceService;
    }

    public RecommendationResponse chat(String userId, String userMessage) {

        List<Book> similarBooks =
                retrievalService.findSimilarBooksByTextAndSource(
                        userMessage,
                        "VA",
                        CANDIDATE_LIMIT
                );

        List<String> rejectedBookIds =
                feedbackService.getRejectedBooks(userId)
                        .stream()
                        .map(feedback -> feedback.getBookId())
                        .toList();

        List<Book> filteredBooks =
                similarBooks.stream()
                        .filter(book ->
                                !rejectedBookIds.contains(book.getRecordId()))
                        .toList();

        String context = filteredBooks.stream()
                .map(b -> "- Record ID: %s  Title: %s  Author: %s  Description: %s".formatted(
                        b.getRecordId(),
                        b.getTitle(),
                        String.join(", ", b.getAuthorsRaw()),
                        b.getDescription()))
                .collect(Collectors.joining("\n"));

        if (context.isBlank()) {
            context = "No relevant books were found.\n";
        }

        String preferenceContext =
                preferenceService.buildPreferenceContext(userId);

        return aiService.chat(
                userMessage,
                context,
                preferenceContext
        );
    }
}