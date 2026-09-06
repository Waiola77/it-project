package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookRetrievalServiceTest {

    @Autowired
    private BookRetrievalService retrievalService;

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    @Transactional
    void shouldFindSimilarBooksFromUserQuery() {

        String userQuery = "I want a fantasy adventure story";

        String queryVector =
                embeddingService.embedAsVectorString(userQuery);

        List<Book> results =
                retrievalService.findSimilarBooks(
                        queryVector,
                        3
                );

        assertEquals(3, results.size());

        System.out.println("Recommendation results:");

        for (Book book : results) {
            System.out.println(book.getTitle());
        }
    }
}