package com.devDynamics.metricsApi.controller;

import com.devDynamics.metricsApi.client.PagerDutyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/pagerduty")
public class PagerDutyController {

    private final PagerDutyClient pagerDutyClient;

    public PagerDutyController(PagerDutyClient pagerDutyClient) {
        this.pagerDutyClient = pagerDutyClient;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchIncidents(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        pagerDutyClient.fetchAndSaveIncidents(from, to);
        return ResponseEntity.ok("Incidents fetched and saved.");
    }
}
