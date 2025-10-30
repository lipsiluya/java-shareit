package ru.practicum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")  // ← добавить корневой путь
    public String root() {
        return "ShareIt Application is running";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}