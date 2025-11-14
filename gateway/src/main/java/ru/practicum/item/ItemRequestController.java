package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.exception.ValidationException;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestClient.createRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(value = USER_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @PathVariable Long requestId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestClient.getRequestById(userId, requestId);
    }
}