package com.devDynamics.metricsApi.repository;

import com.devDynamics.metricsApi.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, String> {
//    List<Incident> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT i FROM Incident i WHERE i.createdAt BETWEEN :from AND :to")
    List<Incident> findByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
