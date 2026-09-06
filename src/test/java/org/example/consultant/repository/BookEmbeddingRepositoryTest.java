package org.example.consultant.repository;

import org.example.consultant.model.Book;
import org.example.consultant.service.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookEmbeddingRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void shouldGenerateAndSaveBookEmbeddings() {

        List<String> recordIds = List.of(
                "vec-001",
                "vec-002",
                "vec-003"
        );

        for (String recordId : recordIds) {

            Book book = bookRepository.findById(recordId).orElseThrow();

            String text = book.getTitle() + ". " + book.getDescription();

            String embedding =
                    embeddingService.embedAsVectorString(text);

            bookRepository.updateEmbedding(
                    recordId,
                    embedding
            );

            assertTrue(embedding.startsWith("["));
            assertTrue(embedding.endsWith("]"));
        }
    }
}