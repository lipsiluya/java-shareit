package ru.practicum.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.comment.CommentDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                         @Valid @RequestBody ItemDto dto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @Valid @RequestBody ItemDto dto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable("itemId") Long itemId,
                                      @RequestHeader(value = USER_HEADER, required = false) Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(value = USER_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return itemClient.addComment(userId, itemId, commentDto);
    }
}