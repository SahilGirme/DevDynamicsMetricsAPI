package com.devDynamics.metricsApi.client;

import com.devDynamics.metricsApi.model.Incident;
import com.devDynamics.metricsApi.repository.IncidentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;

import java.time.*;

@Component
public class PagerDutyClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final IncidentRepository incidentRepository;

    @Value("${pagerduty.api.token}")
    private String apiToken;

    @Value("${pagerduty.api.url}")
    private String apiUrl;

    public PagerDutyClient(RestTemplate restTemplate, ObjectMapper objectMapper, IncidentRepository incidentRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> fetchAndSaveIncidents(LocalDate fromDate, LocalDate toDate) {
        List<Incident> incidents = new ArrayList<>();
        try {
            // Convert LocalDate to Instant in UTC and format as ISO_INSTANT string
            Instant sinceInstant = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant untilInstant = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

            String since = DateTimeFormatter.ISO_INSTANT.format(sinceInstant);
            String until = DateTimeFormatter.ISO_INSTANT.format(untilInstant);

            String url = apiUrl + "?since=" + since + "&until=" + until + "&statuses[]=resolved";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiToken);
            headers.set("Accept", "application/vnd.pagerduty+json;version=2");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode incidentsArray = root.path("incidents");
            for (JsonNode incidentNode : incidentsArray) {
                String id = incidentNode.path("id").asText();
                String createdAt = incidentNode.path("created_at").asText(null);
                String resolvedAt = incidentNode.path("resolved_at").asText(null);

                if (createdAt != null && resolvedAt != null) {
                    Incident incident = new Incident();
                    incident.setId(id);
                    incident.setCreatedAt(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME));
                    incident.setResolvedAt(LocalDateTime.parse(resolvedAt, DateTimeFormatter.ISO_DATE_TIME));
                    incidents.add(incident);
                }
            }

            incidentRepository.saveAll(incidents);
            System.out.println("Saved " + incidents.size() + " incidents from PagerDuty.");

        } catch (Exception e) {
            System.err.println("Error fetching incidents from PagerDuty: " + e.getMessage());
            e.printStackTrace();
        }
        return incidents;
    }


}
