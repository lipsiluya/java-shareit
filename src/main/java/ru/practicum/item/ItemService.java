package ru.practicum.item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);

    ItemDto update(Long ownerId, Long itemId, ItemDto dto);

    ItemDto get(Long itemId);

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String text);
}