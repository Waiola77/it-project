package org.example.consultant.aiservices;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.consultant.model.SearchIntent;

@AiService
public interface IntentExtractionService {

    @UserMessage("""
            Analyze the user's message about books and extract structured search intent.

            Rules:
            - semanticQuery: always fill this with the general theme/genre/mood the user wants
            - title: only fill if the user explicitly names a specific book title, otherwise leave null
            - author: only fill if the user explicitly names a specific author, otherwise leave null
            - source: only fill if the user explicitly asks to filter by a catalogue/source, otherwise leave null

            User message: {{message}}
            """)
    SearchIntent extractIntent(@V("message") String message);
}