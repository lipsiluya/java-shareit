package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ShareItApplication {
    public static void main(String[] args) {
        log.info("🚀 Starting ShareIt Application...");
        try {
            SpringApplication.run(ShareItApplication.class, args);
            log.info("✅ ShareIt Application started successfully on port 8080!");
        } catch (Exception e) {
            log.error("❌ Failed to start ShareIt Application", e);
            System.exit(1);
        }
    }
}