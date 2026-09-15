package org.example.consultant.model;

import dev.langchain4j.model.output.structured.Description;

public class SearchIntent {

    @Description("The book theme, mood, or genre preference described by the user, used for semantic search, e.g. 'a heartwarming mystery novel'")
    private String semanticQuery;

    @Description("The specific book title explicitly mentioned by the user; leave blank if not mentioned")
    private String title;

    @Description("The specific author explicitly mentioned by the user; leave blank if not mentioned")
    private String author;

    @Description("The specific catalogue/source the user explicitly asked to filter by; leave blank if not mentioned")
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