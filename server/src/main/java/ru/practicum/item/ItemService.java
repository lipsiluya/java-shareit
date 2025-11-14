package ru.practicum.item;

import ru.practicum.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);

    ItemDto update(Long ownerId, Long itemId, ItemDto dto);

    ItemDto get(Long itemId);

    ItemDto getItemForUser(Long itemId, Long userId); // ДОБАВИТЬ этот метод

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);


}