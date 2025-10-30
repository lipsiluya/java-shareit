package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//fffff
@Slf4j
@Service
public class ExampleService {
    @Value("${app.name:LaterDefault}")
    private String appName;

    public int sum(int a, int b) {
        log.info("App = {}", appName);
        log.info("Got a={}, b={}", a, b);
        return a + b;
    }
}