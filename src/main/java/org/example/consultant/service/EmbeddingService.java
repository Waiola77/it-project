package org.example.consultant.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService() {
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public float[] embed(String text) {
        Embedding embedding = embeddingModel.embed(text).content();
        return embedding.vector();
    }

    public String embedAsVectorString(String text) {
        float[] vector = embed(text);

        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(vector[i]);
        }
        builder.append("]");

        return builder.toString();
    }
}