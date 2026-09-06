package org.example.consultant.aiservices;

import dev.langchain4j.service.spring.AiService;

@AiService
public interface BookRecommendationServices {

    public String chat(String message);

}
