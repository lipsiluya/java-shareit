package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.user.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";
    private final String serverUrl;

    @Autowired
    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        String url = serverUrl + API_PREFIX;
        return post(url, userDto); // Без userId для создания
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        String url = serverUrl + API_PREFIX + "/" + userId;
        return patch(url, userId, userDto); // userId в заголовок
    }

    public ResponseEntity<Object> getUser(Long userId) {
        String url = serverUrl + API_PREFIX + "/" + userId;
        return get(url, userId); // userId в заголовок
    }

    public ResponseEntity<Object> getAllUsers() {
        String url = serverUrl + API_PREFIX;
        return get(url); // Без userId для получения всех
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        String url = serverUrl + API_PREFIX + "/" + userId;
        return delete(url, userId); // userId в заголовок
    }
}