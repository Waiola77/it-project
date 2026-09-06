package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookNormalizationService {

    public String normalizeTitle(String title) {
        if (title == null) {
            return null;
        }

        return title
                .toLowerCase()
                .trim()
                .replaceAll("[\\p{Punct}]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String normalizeAuthor(String author) {
        if (author == null) {
            return null;
        }

        return author
                .toLowerCase()
                .trim()
                .replaceAll("[\\p{Punct}]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> normalizeAuthors(List<String> authors) {
        List<String> normalizedAuthors = new ArrayList<>();

        if (authors == null) {
            return normalizedAuthors;
        }

        for (String author : authors) {
            normalizedAuthors.add(normalizeAuthor(author));
        }

        return normalizedAuthors;
    }

    public boolean hasUsableDescription(String description) {
        if (description == null) {
            return false;
        }

        String cleaned = description.trim();

        if (cleaned.isEmpty()) {
            return false;
        }

        return !cleaned.equalsIgnoreCase("No synopsis provided.");
    }

    public Book normalizeBook(Book book) {

        book.setNormalizedTitle(
                normalizeTitle(book.getTitle())
        );

        book.setNormalizedAuthors(
                normalizeAuthors(book.getAuthorsRaw())
        );

        book.setHasUsableDescription(
                hasUsableDescription(book.getDescription())
        );

        return book;
    }
}