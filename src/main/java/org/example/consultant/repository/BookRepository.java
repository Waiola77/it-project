package org.example.consultant.repository;

import org.example.consultant.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    @Query(value = """
            SELECT b.*
            FROM books b
            WHERE b.embedding IS NOT NULL
            ORDER BY b.embedding <=> CAST(:queryVector AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Book> findSimilarBooks(
            @Param("queryVector") String queryVector,
            @Param("limit") int limit
    );

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = """
            UPDATE books
            SET embedding = CAST(:embedding AS vector)
            WHERE record_id = :recordId
            """, nativeQuery = true)
    void updateEmbedding(
            @Param("recordId") String recordId,
            @Param("embedding") String embedding
    );

    @Query(value = """
        SELECT b.*
        FROM books b
        WHERE b.embedding IS NOT NULL
          AND b.source = :source
        ORDER BY b.embedding <=> CAST(:queryVector AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<Book> findSimilarBooksBySource(
            @Param("queryVector") String queryVector,
            @Param("source") String source,
            @Param("limit") int limit
    );

}