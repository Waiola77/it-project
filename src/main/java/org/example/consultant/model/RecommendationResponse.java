package org.example.consultant.model;

import java.util.List;

public class RecommendationResponse {

    private List<BookRecommendation> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(List<BookRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<BookRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<BookRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}