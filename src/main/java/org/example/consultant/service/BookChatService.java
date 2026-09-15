package org.example.consultant.service;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.example.consultant.aiservices.IntentExtractionService;
import org.example.consultant.model.Book;
import org.example.consultant.model.SearchIntent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookChatService {

    private final BookRetrievalService retrievalService;
    private final BookRecommendationServices aiService;
    private final IntentExtractionService intentExtractionService;
    private final BookNormalizationService normalizationService;

    public BookChatService(BookRetrievalService retrievalService,
                           BookRecommendationServices aiService,
                           IntentExtractionService intentExtractionService,
                           BookNormalizationService normalizationService) {
        this.retrievalService = retrievalService;
        this.aiService = aiService;
        this.intentExtractionService = intentExtractionService;
        this.normalizationService = normalizationService;
    }

    public String chat(String userMessage) {

        SearchIntent intent = intentExtractionService.extractIntent(userMessage);

        List<Book> similarBooks;

        boolean hasExactTarget =
                (intent.getTitle() != null && !intent.getTitle().isBlank())
                        || (intent.getAuthor() != null && !intent.getAuthor().isBlank());

        if (hasExactTarget) {
            String normalizedTitle = normalizationService.normalizeTitle(intent.getTitle());
            String normalizedAuthor = normalizationService.normalizeAuthor(intent.getAuthor());
            similarBooks = retrievalService.findByTitleOrAuthor(normalizedTitle, normalizedAuthor);
        } else {
            String queryText = (intent.getSemanticQuery() != null && !intent.getSemanticQuery().isBlank())
                    ? intent.getSemanticQuery()
                    : userMessage;
            similarBooks = retrievalService.findSimilarBooksByText(queryText, 5);
        }

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