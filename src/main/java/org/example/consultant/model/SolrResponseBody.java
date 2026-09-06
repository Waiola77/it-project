package org.example.consultant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolrResponseBody {

    private int numFound;
    private List<SolrBookDocument> docs;

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public List<SolrBookDocument> getDocs() {
        return docs;
    }

    public void setDocs(List<SolrBookDocument> docs) {
        this.docs = docs;
    }
}