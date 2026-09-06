package org.example.consultant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;

import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    private String recordId;

    private String title;

    @ElementCollection
    @CollectionTable(
            name = "book_authors_raw",
            joinColumns = @JoinColumn(name = "record_id")
    )
    @Column(name = "author")
    private List<String> authorsRaw;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String source;
    private String normalizedTitle;

    @ElementCollection
    @CollectionTable(
            name = "book_normalized_authors",
            joinColumns = @JoinColumn(name = "record_id")
    )

    @Column(name = "author")
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