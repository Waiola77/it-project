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
        boolean shouldIngest = false;

        for (String arg : args) {
            if ("--ingest-all".equals(arg)) {
                shouldIngest = true;
                break;
            }
        }

        if (!shouldIngest) {
            return;
        }

        System.out.println("Starting full catalogue ingestion...");

        ingestionService.ingestAllBooks(500);

        System.out.println("Full catalogue ingestion finished.");
    }
}