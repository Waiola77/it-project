package org.example.consultant.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiServiceConfig {
    @Autowired
    private OpenAiChatModel model;


}
