package ru.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Item> findByUserId(long userId) {
        return storage.values().stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idGenerator.getAndIncrement());
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Item item = storage.get(itemId);
        if (item != null && Objects.equals(item.getUserId(), userId)) {
            storage.remove(itemId);
        }
    }

    @Override
    public List<Item> search(String text) {
        log.info("Searching items with text: '{}'", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lower = text.toLowerCase().trim();

        List<Item> result = storage.values().stream()
                .filter(i -> i != null) // защита от null items
                .filter(i -> Boolean.TRUE.equals(i.getAvailable())) // безопасная проверка available
                .filter(i -> {
                    // безопасная проверка полей
                    String name = i.getName();
                    String description = i.getDescription();
                    return (name != null && name.toLowerCase().contains(lower))
                            || (description != null && description.toLowerCase().contains(lower));
                })
                .collect(Collectors.toList());

        log.info("Found {} items in repository", result.size());
        return result;
    }
}