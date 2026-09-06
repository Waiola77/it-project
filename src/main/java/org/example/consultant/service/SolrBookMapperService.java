package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.SolrBookDocument;
import org.springframework.stereotype.Service;

@Service
public class SolrBookMapperService {

    private final BookNormalizationService normalizationService;

    public SolrBookMapperService(BookNormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    public Book toBook(SolrBookDocument document) {

        Book book = new Book();

        book.setRecordId(document.getId());
        book.setTitle(document.getTitle());
        book.setAuthorsRaw(document.getAuthors());
        book.setDescription(document.getDescription());
        book.setSource(document.getSource());

        return normalizationService.normalizeBook(book);
    }
}