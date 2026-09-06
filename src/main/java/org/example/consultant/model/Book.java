package org.example.consultant.model;

import java.util.List;

public class Book {

    private String recordId;
    private String title;
    private List<String> authorsRaw;
    private String description;
    private String source;

    private String normalizedTitle;
    private List<String> normalizedAuthors;
    private boolean hasUsableDescription;

    public Book() {
    }

    public Book(String recordId,
                String title,
                List<String> authorsRaw,
                String description,
                String source,
                String normalizedTitle,
                List<String> normalizedAuthors,
                boolean hasUsableDescription) {

        this.recordId = recordId;
        this.title = title;
        this.authorsRaw = authorsRaw;
        this.description = description;
        this.source = source;
        this.normalizedTitle = normalizedTitle;
        this.normalizedAuthors = normalizedAuthors;
        this.hasUsableDescription = hasUsableDescription;
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

    public List<String> getAuthorsRaw() {
        return authorsRaw;
    }

    public void setAuthorsRaw(List<String> authorsRaw) {
        this.authorsRaw = authorsRaw;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    public List<String> getNormalizedAuthors() {
        return normalizedAuthors;
    }

    public void setNormalizedAuthors(List<String> normalizedAuthors) {
        this.normalizedAuthors = normalizedAuthors;
    }

    public boolean isHasUsableDescription() {
        return hasUsableDescription;
    }

    public void setHasUsableDescription(boolean hasUsableDescription) {
        this.hasUsableDescription = hasUsableDescription;
    }
}