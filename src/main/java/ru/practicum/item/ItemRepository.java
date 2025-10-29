package ru.practicum.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findByUserId(long userId);

    Optional<Item> findById(long id);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    List<Item> search(String text);
}