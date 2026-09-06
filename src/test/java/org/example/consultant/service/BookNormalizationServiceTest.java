package org.example.consultant.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookNormalizationServiceTest {

    private final BookNormalizationService service =
            new BookNormalizationService();

    @Test
    void shouldNormalizeTitle() {
        String result = service.normalizeTitle("The   Hobbit!");

        assertEquals("the hobbit", result);
    }

    @Test
    void shouldNormalizeAuthor() {
        String result = service.normalizeAuthor("J. R. R. Tolkien");

        assertEquals("j r r tolkien", result);
    }

    @Test
    void shouldNormalizeAuthors() {
        List<String> result = service.normalizeAuthors(
                List.of("J. R. R. Tolkien", "Brian Sibley")
        );

        assertEquals(
                List.of("j r r tolkien", "brian sibley"),
                result
        );
    }

    @Test
    void shouldRejectMissingDescription() {
        assertFalse(service.hasUsableDescription(null));
        assertFalse(service.hasUsableDescription(""));
        assertFalse(
                service.hasUsableDescription("No synopsis provided.")
        );
    }

    @Test
    void shouldAcceptNormalDescription() {
        assertTrue(
                service.hasUsableDescription(
                        "Bilbo Baggins goes on an unexpected adventure."
                )
        );
    }
}