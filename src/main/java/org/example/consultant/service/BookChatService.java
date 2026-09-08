package org.example.consultant.service;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.example.consultant.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookChatService {

    private final BookRetrievalService retrievalService;
    private final BookRecommendationServices aiService;

    public BookChatService(BookRetrievalService retrievalService,
                           BookRecommendationServices aiService) {
        this.retrievalService = retrievalService;
        this.aiService = aiService;
    }

    public String chat(String userMessage) {
        List<Book> similarBooks = retrievalService.findSimilarBooksByText(userMessage, 5);

        String context = similarBooks.stream()
                .map(b -> "- %s Author：%s  Description：%s".formatted(
                        b.getTitle(),
                        String.join(",", b.getAuthorsRaw()),
                        b.getDescription()))
                .collect(Collectors.joining("\n"));

        if (context.isBlank()) {
            context = "No relevant books were found.\n";
        }

        return aiService.chat(userMessage, context);
    }
}