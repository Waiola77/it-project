package org.example.consultant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_feedback")
public class UserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String bookId;
    private String feedbackType;

    public UserFeedback() {
    }

    public UserFeedback(String userId, String bookId, String feedbackType) {
        this.userId = userId;
        this.bookId = bookId;
        this.feedbackType = feedbackType;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }
}