package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @RequestBody ItemRequestCreateDto itemRequestCreateDto) { // Убрал @Valid
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(value = USER_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(value = USER_HEADER, required = false) Long userId,
            @PathVariable Long requestId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemRequestService.getRequestById(userId, requestId);
    }
}