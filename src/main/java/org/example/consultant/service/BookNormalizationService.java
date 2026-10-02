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

    public String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        return description
                // Common mojibake caused by UTF-8 text being decoded incorrectly
                .replace("â", "–")
                .replace("â", "—")
                .replace("â", "’")
                .replace("â", "‘")
                .replace("â", "“")
                .replace("â", "”")
                .replace("Â ", " ")
                .replace("Â", "")
                // Clean up whitespace
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String cleanAuthorDisplay(String author) {
        if (author == null) {
            return null;
        }

        return author
                .replace("Ã©", "é")
                .replace("Ã¨", "è")
                .replace("Ã¡", "á")
                .replace("Ã­", "í")
                .replace("Ã³", "ó")
                .replace("Ãº", "ú")
                .replace("Ã±", "ñ")
                .replace("Â", "")
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> cleanAuthorDisplays(List<String> authors) {
        List<String> cleanedAuthors = new ArrayList<>();

        if (authors == null) {
            return cleanedAuthors;
        }

        for (String author : authors) {
            cleanedAuthors.add(cleanAuthorDisplay(author));
        }

        return cleanedAuthors;
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

        book.setAuthorsRaw(
                cleanAuthorDisplays(book.getAuthorsRaw())
        );

        book.setNormalizedTitle(
                normalizeTitle(book.getTitle())
        );

        book.setNormalizedAuthors(
                normalizeAuthors(book.getAuthorsRaw())
        );

        book.setDescription(
                normalizeDescription(book.getDescription())
        );

        book.setHasUsableDescription(
                hasUsableDescription(book.getDescription())
        );

        return book;
    }

}