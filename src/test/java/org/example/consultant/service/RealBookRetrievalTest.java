package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RealBookRetrievalTest {

    @Autowired
    private BookRetrievalService bookRetrievalService;

    @Test
    void shouldRecommendRealBooksBySemanticMeaning() {

        String userQuery =
                "I want a romantic love story";

        List<Book> results =
                bookRetrievalService.findSimilarBooksByTextAndSource(
                        userQuery,
                        "VA",
                        5
                );

        System.out.println("\nUser query: " + userQuery);
        System.out.println("Recommendation results:");

        for (int i = 0; i < results.size(); i++) {
            Book book = results.get(i);

            System.out.println(
                    "\n" + (i + 1) + ". "
                            + book.getTitle()
                            + " | source: "
                            + book.getSource()
            );

            System.out.println(
                    "Description: "
                            + book.getDescription()
            );
        }

        assertFalse(results.isEmpty());

        for (Book book : results) {
            assertEquals("VA", book.getSource());
        }
    }
}