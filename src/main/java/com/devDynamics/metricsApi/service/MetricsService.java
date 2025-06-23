package com.devDynamics.metricsApi.service;

import com.devDynamics.metricsApi.model.Incident;
import com.devDynamics.metricsApi.repository.DeploymentRepository;
import com.devDynamics.metricsApi.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MetricsService {
    @Autowired private IncidentRepository incidentRepo;
    @Autowired
    private DeploymentRepository deployRepo;

    public double calculateCFR(LocalDateTime from, LocalDateTime to) {
        long incidentCount = incidentRepo.countByCreatedAtBetween(from, to);
        long deploymentCount = deployRepo.countByDeployedAtBetween(from, to);
        return deploymentCount == 0 ? 0 : (double) incidentCount / deploymentCount;
    }

    public double calculateMTTR(LocalDateTime from, LocalDateTime to) {
        List<Incident> incidents = incidentRepo.findByCreatedAtBetween(from, to);
        long totalRecoveryMinutes = incidents.stream()
            .filter(i -> i.getResolvedAt() != null)
            .mapToLong(i -> Duration.between(i.getCreatedAt(), i.getResolvedAt()).toMinutes())
            .sum();
        return incidents.isEmpty() ? 0 : (double) totalRecoveryMinutes / incidents.size();
    }
}
