package ru.practicum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    //внесу комментарий сюда, тк забыл раньше создать ветку, простите((
    // Добавляем фильтр для поддержки PATCH методов
    @Bean
    public jakarta.servlet.Filter hiddenHttpMethodFilter() {
        return new org.springframework.web.filter.HiddenHttpMethodFilter();
    }
}