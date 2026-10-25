package org.example.consultant.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class ChatModelConfig {

    @Bean
    OpenAiChatModel chatModel(@Value("${DASHSCOPE_API_KEY}") String apiKey) {
        return OpenAiChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(apiKey)
                .modelName("deepseek-v4-flash-0731")
                .customParameters(Map.of("enable_thinking", false))
                .timeout(Duration.ofSeconds(120))
                .maxRetries(0)
                .build();
    }
}