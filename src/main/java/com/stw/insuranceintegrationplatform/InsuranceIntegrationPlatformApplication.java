package com.stw.insuranceintegrationplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InsuranceIntegrationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsuranceIntegrationPlatformApplication.class, args);
    }
}
