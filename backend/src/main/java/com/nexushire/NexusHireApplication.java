package com.nexushire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NexusHireApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusHireApplication.class, args);
    }
}
