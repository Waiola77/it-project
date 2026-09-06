package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookRetrievalService {

    private final BookRepository bookRepository;

    public BookRetrievalService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findSimilarBooks(String queryVector, int limit) {
        return bookRepository.findSimilarBooks(queryVector, limit);
    }
}