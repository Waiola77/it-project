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

    public BookChatService(BookRetrievalService retrievalService,
                           BookRecommendationServices aiService) {
        this.retrievalService = retrievalService;
        this.aiService = aiService;
    }

    public RecommendationResponse chat(String userMessage) {
        List<Book> similarBooks = retrievalService.findSimilarBooksByText(userMessage, CANDIDATE_LIMIT);

        String context = similarBooks.stream()
                .map(b -> "- Record ID: %s  Title: %s  Author: %s  Description: %s".formatted(
                        b.getRecordId(),
                        b.getTitle(),
                        String.join(", ", b.getAuthorsRaw()),
                        b.getDescription()))
                .collect(Collectors.joining("\n"));

        if (context.isBlank()) {
            context = "No relevant books were found.\n";
        }

        return aiService.chat(userMessage, context);
    }
}