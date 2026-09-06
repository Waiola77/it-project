package org.example.consultant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolrResponse {

    private SolrResponseBody response;

    public SolrResponseBody getResponse() {
        return response;
    }

    public void setResponse(SolrResponseBody response) {
        this.response = response;
    }
}