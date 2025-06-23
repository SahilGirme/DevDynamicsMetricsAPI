package com.devDynamics.metricsApi.repository;

import com.devDynamics.metricsApi.model.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    long countByDeployedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Deployment> findByDeployedAtBetween(LocalDateTime from, LocalDateTime to);
}
