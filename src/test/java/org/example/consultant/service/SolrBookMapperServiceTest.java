package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.SolrBookDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolrBookMapperServiceTest {

    private final BookNormalizationService normalizationService =
            new BookNormalizationService();

    private final SolrBookMapperService mapperService =
            new SolrBookMapperService(normalizationService);

    @Test
    void shouldMapSolrDocumentToBook() {

        SolrBookDocument document = new SolrBookDocument();
        document.setId("solr-001");
        document.setTitle("The Hobbit!");
        document.setAuthors(List.of("J. R. R. Tolkien"));
        document.setDescription("A fantasy adventure about a hobbit.");
        document.setSource("client-solr");

        Book book = mapperService.toBook(document);

        assertEquals("solr-001", book.getRecordId());
        assertEquals("The Hobbit!", book.getTitle());
        assertEquals(List.of("J. R. R. Tolkien"), book.getAuthorsRaw());
        assertEquals("client-solr", book.getSource());

        assertEquals("the hobbit", book.getNormalizedTitle());
        assertEquals(List.of("j r r tolkien"), book.getNormalizedAuthors());
        assertTrue(book.isHasUsableDescription());
    }
}