package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                          @RequestBody ItemDto dto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return service.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody ItemDto dto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return service.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") Long itemId) {
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(value = USER_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return service.getByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }
}