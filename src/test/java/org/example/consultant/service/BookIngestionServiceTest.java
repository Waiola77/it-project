package org.example.consultant.service;

import org.example.consultant.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookIngestionServiceTest {

    @Autowired
    private BookIngestionService ingestionService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldIngestBooksInBatches() {

        ingestionService.ingestBooksInBatches(100, 20);

        long count = bookRepository.count();

        System.out.println("Total books in database: " + count);

        assertTrue(count >= 100);
    }
}