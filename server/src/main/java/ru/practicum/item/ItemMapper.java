package ru.practicum.item;

import ru.practicum.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final ItemRequestRepository itemRequestRepository;

    public static Item toItem(ItemDto dto, Long ownerId) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setUserId(ownerId);
        return item;
    }

    public Item toItemWithRequest(ItemDto dto, Long ownerId) {
        Item item = toItem(dto, ownerId);

        // Если указан requestId, устанавливаем связь с запросом
        if (dto.getRequestId() != null) {
            var request = itemRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request not found with id: " + dto.getRequestId()));
            item.setRequest(request);
        }

        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getUserId());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        // Комментарии будут добавлены в сервисе, но инициализируем список
        dto.setComments(new ArrayList<>());

        return dto;
    }
}