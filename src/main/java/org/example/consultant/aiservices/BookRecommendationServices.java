package org.example.consultant.aiservices;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.consultant.model.BookRecommendation;
import java.util.List;
import org.example.consultant.model.RecommendationResponse;

@AiService
public interface BookRecommendationServices {

    @SystemMessage("""
            You are a book recommendation assistant.

            Rank the candidate books according to how well they match the user's request.

            Rules:
            - Only use books from the candidate list.
            - Do not make up or recommend books outside the candidate list.
            - Return up to 10 recommendations.
            - Rank them from the best match to the weakest match.
            - Briefly explain why each book matches the user's request.
            - If no candidate book is suitable, say that no suitable book was found.

            Candidate books:
            {{context}}
            """)
    @UserMessage("{{message}}")
    RecommendationResponse chat(@V("message") String message,
                                @V("context") String context);
}