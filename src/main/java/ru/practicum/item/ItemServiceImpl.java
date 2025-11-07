package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.*;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        if (ownerId == null || ownerId <= 0) {
            throw new ValidationException("Invalid owner ID");
        }

        validateItemDto(dto);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        Item item = ItemMapper.toItem(dto, ownerId);
        Item saved = itemRepository.save(item);
        log.info("Item created: {}", saved.getId());
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        if (ownerId == null || ownerId <= 0) {
            throw new ValidationException("Invalid owner ID");
        }
        if (itemId == null || itemId <= 0) {
            throw new ValidationException("Invalid item ID");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        if (!item.getUserId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item");
        }

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new ValidationException("Item name cannot be empty");
            }
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            if (dto.getDescription().isBlank()) {
                throw new ValidationException("Item description cannot be empty");
            }
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        Item updated = itemRepository.save(item);
        log.info("Item updated: {}", updated.getId());
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto get(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new ValidationException("Invalid item ID");
        }

        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new ValidationException("Invalid owner ID");
        }

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        return itemRepository.findByUserId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Searching items with text: '{}'", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Item> foundItems = itemRepository.search(text);
        log.info("Found {} items", foundItems.size());

        return foundItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Item name is required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Item description is required");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Item availability must be specified");
        }
    }
}