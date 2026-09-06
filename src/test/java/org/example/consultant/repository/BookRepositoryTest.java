package org.example.consultant.repository;

import org.example.consultant.model.Book;
import org.example.consultant.service.BookNormalizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookNormalizationService normalizationService;

    @Test
    @Transactional
    void shouldSaveAndReadBook() {

        Book book = new Book(
                "test-001",
                "The Hobbit",
                List.of("J. R. R. Tolkien"),
                "A hobbit goes on an unexpected adventure.",
                "test",
                null,
                null,
                false
        );

        normalizationService.normalizeBook(book);

        bookRepository.save(book);

        Book savedBook = bookRepository
                .findById("test-001")
                .orElseThrow();

        assertEquals("The Hobbit", savedBook.getTitle());
        assertEquals("the hobbit", savedBook.getNormalizedTitle());
        assertEquals(
                List.of("j r r tolkien"),
                savedBook.getNormalizedAuthors()
        );
        assertTrue(savedBook.isHasUsableDescription());
    }
}