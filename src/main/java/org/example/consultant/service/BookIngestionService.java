package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.SolrBookDocument;
import org.example.consultant.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookIngestionService {

    private final SolrClientService solrClientService;
    private final SolrBookMapperService mapperService;
    private final BookRepository bookRepository;
    private final EmbeddingService embeddingService;

    public BookIngestionService(
            SolrClientService solrClientService,
            SolrBookMapperService mapperService,
            BookRepository bookRepository,
            EmbeddingService embeddingService) {

        this.solrClientService = solrClientService;
        this.mapperService = mapperService;
        this.bookRepository = bookRepository;
        this.embeddingService = embeddingService;
    }

    public void ingestBooks(int rows) {

        List<SolrBookDocument> documents =
                solrClientService.fetchBooks(0, rows);

        for (SolrBookDocument document : documents) {

            Book book = mapperService.toBook(document);

            bookRepository.save(book);

            if (book.isHasUsableDescription()) {

                String text =
                        book.getTitle() + ". " + book.getDescription();

                String embedding =
                        embeddingService.embedAsVectorString(text);

                bookRepository.updateEmbedding(
                        book.getRecordId(),
                        embedding
                );
            }
        }
    }

    public void ingestBooksInBatches(int totalBooks, int batchSize) {

        int start = 0;

        while (start < totalBooks) {

            int rows = Math.min(batchSize, totalBooks - start);

            List<SolrBookDocument> documents =
                    solrClientService.fetchBooks(start, rows);

            if (documents.isEmpty()) {
                break;
            }

            for (SolrBookDocument document : documents) {

                Book book = mapperService.toBook(document);

                bookRepository.save(book);

                if (book.isHasUsableDescription()) {

                    String text =
                            book.getTitle() + ". " + book.getDescription();

                    String embedding =
                            embeddingService.embedAsVectorString(text);

                    bookRepository.updateEmbedding(
                            book.getRecordId(),
                            embedding
                    );
                }
            }

            start += documents.size();
        }
    }
}