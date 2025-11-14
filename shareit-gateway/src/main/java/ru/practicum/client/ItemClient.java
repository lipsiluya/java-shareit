package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.item.ItemDto;
import ru.practicum.item.comment.CommentDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    private final String serverUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        String url = serverUrl + API_PREFIX;
        return post(url, userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        String url = serverUrl + API_PREFIX + "/" + itemId;
        return patch(url, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        String url = serverUrl + API_PREFIX + "/" + itemId;
        return get(url, userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        String url = serverUrl + API_PREFIX;
        return get(url, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        String url = serverUrl + API_PREFIX + "/" + itemId + "/comment";
        return post(url, userId, commentDto);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String url = serverUrl + API_PREFIX + "/search?text=" + text;
        return get(url);
    }

    // Вспомогательный метод для создания заголовков
    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", userId.toString());
        }
        return headers;
    }
}