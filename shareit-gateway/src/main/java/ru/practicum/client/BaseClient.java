package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        try {
            if (parameters != null && !parameters.isEmpty()) {
                return rest.exchange(path, HttpMethod.GET, requestEntity, Object.class, parameters);
            } else {
                return rest.exchange(path, HttpMethod.GET, requestEntity, Object.class);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        }
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return post(path, null, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        HttpHeaders headers = createHeaders(userId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            return rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        }
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        HttpHeaders headers = createHeaders(userId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            return rest.exchange(path, HttpMethod.PATCH, requestEntity, Object.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        }
    }

    protected ResponseEntity<Object> patch(String path, Long userId) {
        return patch(path, userId, null);
    }

    // ДОБАВИТЬ: Метод delete
    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        try {
            return rest.exchange(path, HttpMethod.DELETE, requestEntity, Object.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        }
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}