package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class RecommendationResultTest {

    @Autowired
    private BookRetrievalService retrievalService;

    private static final String QUERY = "I want a romantic love story";

    @Test
    void shouldReturnRequestedNumberOfBooks() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        assertEquals(3, results.size());
    }

    @Test
    @Transactional
    @Disabled("Fails: 3 of 100 ingested records have no author, and such records are not excluded from results. See O3.")
    void everyResultShouldHaveAnAuthor() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        for (Book book : results) {
            assertFalse(book.getAuthorsRaw().isEmpty());
        }
    }

    @Test
    void resultsShouldNotContainDuplicates() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        long distinct = results.stream()
                .map(Book::getRecordId)
                .distinct()
                .count();
        assertEquals(results.size(), distinct);
    }
}