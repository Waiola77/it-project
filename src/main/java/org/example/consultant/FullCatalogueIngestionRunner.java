package org.example.consultant;

import org.example.consultant.service.BookIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FullCatalogueIngestionRunner implements CommandLineRunner {

    private final BookIngestionService ingestionService;

    public FullCatalogueIngestionRunner(BookIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(String... args) {

        for (String arg : args) {

            if ("--ingest-1000".equals(arg)) {
                System.out.println("Starting 1000-book ingestion...");
                ingestionService.ingestBooksInBatches(1000, 500);
                System.out.println("1000-book ingestion finished.");
                return;
            }

            if ("--ingest-all".equals(arg)) {
                System.out.println("Starting full catalogue ingestion...");
                ingestionService.ingestAllBooks(500);
                System.out.println("Full catalogue ingestion finished.");
                return;
            }
        }
    }
}