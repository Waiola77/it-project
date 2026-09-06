package org.example.consultant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.example.consultant.model.SolrBookDocument;
import org.example.consultant.model.SolrResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class SolrClientService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SolrClientService() {

        String username = System.getenv("SOLR_USERNAME");
        String password = System.getenv("SOLR_PASSWORD");

        this.restClient = RestClient.builder()
                .baseUrl("https://solr-staging.realsam.co.uk")
                .defaultHeaders(headers ->
                        headers.setBasicAuth(username, password)
                )
                .build();
    }

    public String fetchRawBooks(int rows) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/solr/VA/select")
                        .queryParam("q", "*:*")
                        .queryParam("rows", rows)
                        .queryParam("wt", "json")
                        .build())
                .retrieve()
                .body(String.class);
    }

    public List<SolrBookDocument> fetchBooks(int start, int rows) {

        String json = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/solr/VA/select")
                        .queryParam("q", "*:*")
                        .queryParam("start", start)
                        .queryParam("rows", rows)
                        .queryParam("wt", "json")
                        .build())
                .retrieve()
                .body(String.class);

        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            SolrResponse result =
                    objectMapper.readValue(json, SolrResponse.class);

            if (result.getResponse() == null ||
                    result.getResponse().getDocs() == null) {
                return List.of();
            }

            return result.getResponse().getDocs();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse Solr response",
                    e
            );
        }
    }
}