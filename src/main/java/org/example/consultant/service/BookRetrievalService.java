package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookRetrievalService {

    private final BookRepository bookRepository;
    private final EmbeddingService embeddingService;

    public BookRetrievalService(
            BookRepository bookRepository,
            EmbeddingService embeddingService) {

        this.bookRepository = bookRepository;
        this.embeddingService = embeddingService;
    }

    public List<Book> findSimilarBooks(
            String queryVector,
            int limit) {

        return bookRepository.findSimilarBooks(
                queryVector,
                limit
        );
    }

    public List<Book> findSimilarBooksByText(
            String userQuery,
            int limit) {

        String queryVector =
                embeddingService.embedAsVectorString(userQuery);

        return bookRepository.findSimilarBooks(
                queryVector,
                limit
        );
    }

    public List<Book> findSimilarBooksByTextAndSource(
            String userQuery,
            String source,
            int limit) {

        String queryVector =
                embeddingService.embedAsVectorString(userQuery);

        return bookRepository.findSimilarBooksBySource(
                queryVector,
                source,
                limit
        );
    }
}