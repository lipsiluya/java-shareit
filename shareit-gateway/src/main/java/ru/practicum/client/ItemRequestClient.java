package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.item.ItemRequestCreateDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private final String serverUrl;

    @Autowired
    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto requestDto) {
        String url = serverUrl + API_PREFIX;
        return post(url, userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        String url = serverUrl + API_PREFIX;
        return get(url, userId);
    }

    public ResponseEntity<Object> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        String url = serverUrl + API_PREFIX + "/all";

        Map<String, Object> parameters = new HashMap<>();
        if (from != null) parameters.put("from", from);
        if (size != null) parameters.put("size", size);

        return get(url, userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        String url = serverUrl + API_PREFIX + "/" + requestId;
        return get(url, userId);
    }
}