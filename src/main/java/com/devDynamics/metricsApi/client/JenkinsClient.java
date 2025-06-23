package com.devDynamics.metricsApi.client;

import com.devDynamics.metricsApi.model.Deployment;
import com.devDynamics.metricsApi.repository.DeploymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Service
public class JenkinsClient {

    @Value("${jenkins.api.url}")
    private String jenkinsUrl;

    @Value("${jenkins.username}")
    private String username;

    @Value("${jenkins.token}")
    private String token;

    private final DeploymentRepository deploymentRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public JenkinsClient(DeploymentRepository deploymentRepo) {
        this.deploymentRepo = deploymentRepo;
    }

    public void fetchAndStoreDeployments() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // Set Basic Auth headers
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + token;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Append tree param for builds

        String url = jenkinsUrl + "?tree=builds[number,timestamp,result]";
        System.out.println(">>> Request URL: " + url);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        String responseBody = response.getBody();
        System.out.println(">>> Jenkins Response: " + responseBody);

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode buildsArray = root.path("builds");
        if (!buildsArray.isArray() || buildsArray.isEmpty()) {
            System.out.println("No builds found in Jenkins API response.");
            return;
        }

        for (JsonNode build : buildsArray) {
            String result = build.path("result").asText();
            long timestamp = build.path("timestamp").asLong();

            if ("SUCCESS".equalsIgnoreCase(result)) {
                LocalDateTime deployedAt = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                Deployment deployment = new Deployment();
                deployment.setJobName("my-service-deploy");
                deployment.setDeployedAt(deployedAt);

                deploymentRepo.save(deployment);
                System.out.println("Saved deployment: " + deployedAt);
            }
        }
    }
}
