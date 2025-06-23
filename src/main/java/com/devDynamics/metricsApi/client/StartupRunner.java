package com.devDynamics.metricsApi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired JenkinsClient jenkinsClient;

    public void run(String... args) throws JsonProcessingException {
        jenkinsClient.fetchAndStoreDeployments();
    }
}

