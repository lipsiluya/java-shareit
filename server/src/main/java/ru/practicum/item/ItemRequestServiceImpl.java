package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (itemRequestCreateDto.getDescription() == null ||
                itemRequestCreateDto.getDescription().isBlank()) {
            throw new ValidationException("Request description cannot be empty");
        }

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Item request created: {}", savedRequest.getId());

        return ItemRequestMapper.toItemRequestDto(savedRequest, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    List<ItemResponseDto> itemResponses = items.stream()
                            .map(ItemRequestMapper::toItemResponseDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemResponses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        List<ItemRequest> requests;

        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Invalid pagination parameters");
            }
            Pageable pageable = PageRequest.of(from / size, size);
            requests = itemRequestRepository.findOtherUsersRequests(userId, pageable);
        } else {
            requests = itemRequestRepository.findOtherUsersRequests(userId);
        }

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    List<ItemResponseDto> itemResponses = items.stream()
                            .map(ItemRequestMapper::toItemResponseDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemResponses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }
        if (requestId == null || requestId <= 0) {
            throw new ValidationException("Invalid request ID");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + requestId));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemResponses = items.stream()
                .map(ItemRequestMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(request, itemResponses);
    }
}