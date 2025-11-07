package ru.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Убрал кастомный ObjectMapper - Spring Boot создаст его автоматически
    // Убрал configureMessageConverters - используется конфигурация по умолчанию

    // Оставляем только фильтр для PATCH методов
    @Bean
    public jakarta.servlet.Filter hiddenHttpMethodFilter() {
        return new org.springframework.web.filter.HiddenHttpMethodFilter();
    }
}