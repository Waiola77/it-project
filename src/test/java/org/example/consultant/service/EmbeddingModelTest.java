package org.example.consultant.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbeddingModelTest {

    @Test
    void shouldGenerate384DimensionEmbedding() {

        EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();

        Embedding embedding = model.embed(
                "I want a fantasy adventure story"
        ).content();

        System.out.println("Dimension: " + embedding.vector().length);

        assertEquals(384, embedding.vector().length);
    }
}