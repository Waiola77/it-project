package org.example.consultant.model;

public class BookRecommendation {

    private String recordId;
    private String title;
    private String reason;
    private int rank;

    public BookRecommendation() {
    }

    public BookRecommendation(String recordId, String title, String reason, int rank) {
        this.recordId = recordId;
        this.title = title;
        this.reason = reason;
        this.rank = rank;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}