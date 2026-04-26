package org.example.viralityengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone; // 1. Add this import

@SpringBootApplication
@EnableScheduling
public class ViralityEngineApplication {

    public static void main(String[] args) {
        // 2. Add this line to force UTC timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(ViralityEngineApplication.class, args);
    }
}
