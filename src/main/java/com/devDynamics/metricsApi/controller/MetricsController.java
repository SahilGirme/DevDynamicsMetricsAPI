package com.devDynamics.metricsApi.controller;

import com.devDynamics.metricsApi.client.PagerDutyClient;
import com.devDynamics.metricsApi.model.Incident;
import com.devDynamics.metricsApi.repository.IncidentRepository;
import com.devDynamics.metricsApi.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MetricsController {

    @Autowired private MetricsService metricsService;
    @Autowired private PagerDutyClient pagerDutyClient;
    @Autowired private IncidentRepository incidentRepo;

    @GetMapping("/metrics")
    public String metricsForm() {
        return "metrics";  // show the form with no metrics initially
    }

    @GetMapping("/metrics/calculate")
    public String calculateMetrics(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        List<Incident> incidents = pagerDutyClient.fetchAndSaveIncidents(from, to);
        incidentRepo.saveAll(incidents);

        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);

        model.addAttribute("cfr", metricsService.calculateCFR(fromTime, toTime));
        model.addAttribute("mttr", metricsService.calculateMTTR(fromTime, toTime));
        return "metrics";
    }
}

