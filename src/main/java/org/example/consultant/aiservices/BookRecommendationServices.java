package org.example.consultant.aiservices;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

@AiService
public interface BookRecommendationServices {

    @SystemMessage("""
            You are a book recommendation assistant. 
            Answer the user's questions only based on the information about the candidate books provided below. 
            Do not make up or recommend any books that are not in the candidate list. 
            If none of the candidate books are suitable, simply say that no suitable book was found.
            
        Candidate books:{{context}}
        """)
    @UserMessage("{{message}}")
    String chat(@V("message") String message, @V("context") String context);
}