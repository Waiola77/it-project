package org.example.consultant.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class SolrClientServiceTest {

    private final SolrClientService solrClientService =
            new SolrClientService();

    @Test
    void shouldFetchRawBooksFromSolr() {

        String response =
                solrClientService.fetchRawBooks(1);

        System.out.println(response);

        assertNotNull(response);
        assertTrue(response.contains("\"response\""));
        assertTrue(response.contains("\"docs\""));
    }

    @Test
    void shouldFetchBooksAsJavaObjects() {

        var books = solrClientService.fetchBooks(0, 1);

        assertNotNull(books);
        assertEquals(1, books.size());

        var book = books.get(0);

        System.out.println("ID: " + book.getId());
        System.out.println("Title: " + book.getTitle());
        System.out.println("Authors: " + book.getAuthors());
        System.out.println("Source: " + book.getSource());
        System.out.println("Description: " + book.getDescription());

        assertNotNull(book.getId());
        assertNotNull(book.getTitle());
    }
}