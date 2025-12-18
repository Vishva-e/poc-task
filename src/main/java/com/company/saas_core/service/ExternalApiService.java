package com.company.saas_core.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * Simple example of using RestTemplate for external API integration.
 * In a production app you would use timeouts, resilience (retry/circuit-breaker), and DTOs.
 */
@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchPlainText(String url) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to fetch external resource", ex);
        }
    }
}
