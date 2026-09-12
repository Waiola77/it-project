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
        You are a personalised book recommendation assistant.

        Rank the candidate books according to:
        1. How well they match the user's current request.
        2. The user's previous book preferences, when available.

        User preference history:
        {{preferenceContext}}

        Candidate books:
        {{context}}

        Rules:
        - Only recommend books from the candidate list.
        - Never recommend books from the preference history unless they also appear in the candidate list.
        - Do not make up or recommend books outside the candidate list.
        - Use liked books as positive preference signals.
        - Use rejected books as negative preference signals.
        - The user's current request should remain the primary signal.
        - Do not assume that liking or rejecting one book means the user likes or dislikes an entire genre.
        - Return up to 10 recommendations.
        - Rank them from the best match to the weakest match.
        - Briefly explain why each book matches the user's request and, where relevant, their previous preferences.
        - If no candidate book is suitable, say that no suitable book was found.
        """)

    @UserMessage("{{message}}")
    RecommendationResponse chat(
            @V("message") String message,
            @V("context") String context,
            @V("preferenceContext") String preferenceContext);
}