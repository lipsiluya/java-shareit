package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.BookingShortDto;
import ru.practicum.booking.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.comment.Comment;
import ru.practicum.item.comment.CommentDto;
import ru.practicum.item.comment.CommentMapper;
import ru.practicum.item.comment.CommentRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
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
    @Transactional
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

        updateItemFields(item, dto);
        Item updated = itemRepository.save(item);
        log.info("Item updated: {}", updated.getId());
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto get(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new ValidationException("Invalid item ID");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        return enrichItemWithBasicData(item);
    }

    public ItemDto getItemForUser(Long itemId, Long userId) {
        if (itemId == null || itemId <= 0) {
            throw new ValidationException("Invalid item ID");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        if (userId != null && item.getUserId().equals(userId)) {
            return enrichItemWithFullData(item);
        } else {
            return enrichItemWithBasicData(item);
        }
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new ValidationException("Invalid owner ID");
        }

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        // Используем пагинацию для получения items владельца
        Pageable pageable = PageRequest.of(0, 20);
        return itemRepository.findByUserIdOrderById(ownerId, pageable).stream()
                .map(this::enrichItemWithFullData)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Searching items with text: '{}'", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, 20);
        List<Item> foundItems = itemRepository.search(text, pageable);
        log.info("Found {} items", foundItems.size());

        return foundItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (userBookings.isEmpty()) {
            throw new ValidationException("User can only comment on items they have booked in the past");
        }

        // Используем обычное создание вместо билдера
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        String authorName = userRepository.findById(userId)
                .map(User::getName)
                .orElse("Unknown");

        return CommentMapper.toCommentDto(savedComment, authorName);
    }

    private ItemDto enrichItemWithBasicData(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        itemDto.setComments(comments.stream()
                .map(comment -> {
                    String authorName = userRepository.findById(comment.getAuthorId())
                            .map(User::getName)
                            .orElse("Unknown");
                    return CommentMapper.toCommentDto(comment, authorName);
                })
                .collect(Collectors.toList()));

        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);

        return itemDto;
    }

    private ItemDto enrichItemWithFullData(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> itemBookings = bookingRepository.findByItemId(item.getId());

        Booking lastBooking = itemBookings.stream()
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .filter(booking -> booking.getEnd().isBefore(now))
                .max((b1, b2) -> b2.getEnd().compareTo(b1.getEnd()))
                .orElse(null);

        itemDto.setLastBooking(lastBooking != null ?
                new BookingShortDto(lastBooking.getId(), lastBooking.getBookerId(), lastBooking.getStart(), lastBooking.getEnd()) :
                null);

        Booking nextBooking = itemBookings.stream()
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .filter(booking -> booking.getStart().isAfter(now))
                .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .orElse(null);

        itemDto.setNextBooking(nextBooking != null ?
                new BookingShortDto(nextBooking.getId(), nextBooking.getBookerId(), nextBooking.getStart(), nextBooking.getEnd()) :
                null);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        itemDto.setComments(comments.stream()
                .map(comment -> {
                    String authorName = userRepository.findById(comment.getAuthorId())
                            .map(User::getName)
                            .orElse("Unknown");
                    return CommentMapper.toCommentDto(comment, authorName);
                })
                .collect(Collectors.toList()));

        return itemDto;
    }

    private void updateItemFields(Item item, ItemDto dto) {
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