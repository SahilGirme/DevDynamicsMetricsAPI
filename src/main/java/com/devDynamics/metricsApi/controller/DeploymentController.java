package com.devDynamics.metricsApi.controller;

import com.devDynamics.metricsApi.client.JenkinsClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeploymentController {
    @Autowired JenkinsClient jenkinsClient;

    @PostMapping("/deployments/sync")
    public ResponseEntity<String> sync() throws JsonProcessingException {
        jenkinsClient.fetchAndStoreDeployments();
        return ResponseEntity.ok("Deployments synced!");
    }
}
