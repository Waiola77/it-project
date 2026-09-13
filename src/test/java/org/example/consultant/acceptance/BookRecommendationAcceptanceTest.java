package org.example.consultant.acceptance;

import org.example.consultant.model.Book;
import org.example.consultant.service.BookRetrievalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookRecommendationAcceptanceTest {

    @Autowired
    private BookRetrievalService retrievalService;

    private static final String QUERY = "I want a romantic love story";

    @Test
    void shouldReturnRequestedNumberOfBooks() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        assertEquals(3, results.size());
    }

    @Test
    void shouldReturnFiveCandidateBooksForRecommendationResponse() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 5);
        assertEquals(5, results.size());
    }

    @Test
    void fiveCandidateResultsShouldNotContainDuplicates() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 5);
        long distinct = results.stream()
                .map(Book::getRecordId)
                .distinct()
                .count();
        assertEquals(results.size(), distinct);
    }

    @Test
    void shouldReturnOnlyBooksFromRequestedSource() {
        List<Book> results = retrievalService.findSimilarBooksByTextAndSource(
                QUERY,
                "VA",
                5
        );

        assertEquals(5, results.size());
        for (Book book : results) {
            assertEquals("VA", book.getSource());
        }
    }

    @Test
    void shouldReturnResultsForDifferentUserPreferences() {
        List<String> userPreferences = List.of(
                "I want a romantic love story",
                "I want a science fiction adventure",
                "I want a mystery crime story",
                "I want a history or war survival story"
        );

        for (String userPreference : userPreferences) {
            List<Book> results = retrievalService.findSimilarBooksByText(
                    userPreference,
                    3
            );
            assertFalse(results.isEmpty());
        }
    }

    @Test
    void everyResultShouldHaveDisplayableBookDetails() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        for (Book book : results) {
            assertAll(
                    () -> assertNotNull(book.getRecordId()),
                    () -> assertFalse(book.getRecordId().isBlank()),
                    () -> assertNotNull(book.getTitle()),
                    () -> assertFalse(book.getTitle().isBlank()),
                    () -> assertNotNull(book.getSource()),
                    () -> assertFalse(book.getSource().isBlank())
            );
        }
    }

    @Test
    void everyResultShouldHaveAUsableDescription() {
        List<Book> results = retrievalService.findSimilarBooksByText(QUERY, 3);
        for (Book book : results) {
            assertAll(
                    () -> assertNotNull(book.getDescription()),
                    () -> assertFalse(book.getDescription().isBlank()),
                    () -> assertTrue(book.isHasUsableDescription())
            );
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
