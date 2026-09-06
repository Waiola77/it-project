package org.example.consultant.model;

public class SearchIntent {

    private String semanticQuery;
    private String title;
    private String author;
    private String source;

    public SearchIntent() {
    }

    public SearchIntent(String semanticQuery,
                        String title,
                        String author,
                        String source) {
        this.semanticQuery = semanticQuery;
        this.title = title;
        this.author = author;
        this.source = source;
    }

    public String getSemanticQuery() {
        return semanticQuery;
    }

    public void setSemanticQuery(String semanticQuery) {
        this.semanticQuery = semanticQuery;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}