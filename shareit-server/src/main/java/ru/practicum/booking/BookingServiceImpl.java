package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.ItemShortDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) { // userId первым параметром
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        validateBooking(bookingRequestDto, item, userId);

        Booking booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .itemId(item.getId())
                .bookerId(userId)
                .status(Status.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        UserShortDto bookerDto = new UserShortDto(booker.getId(), booker.getName());
        ItemShortDto itemDto = new ItemShortDto(item.getId(), item.getName());

        return BookingMapper.toBookingResponseDto(savedBooking, bookerDto, itemDto);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getUserId().equals(ownerId)) {
            throw new ForbiddenException("Only item owner can approve booking");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Booking status cannot be changed");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker not found"));
        UserShortDto bookerDto = new UserShortDto(booker.getId(), booker.getName());
        ItemShortDto itemDto = new ItemShortDto(item.getId(), item.getName());

        return BookingMapper.toBookingResponseDto(updatedBooking, bookerDto, itemDto);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker not found"));

        boolean isBooker = booking.getBookerId().equals(userId);
        boolean isOwner = item.getUserId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException("Access denied");
        }

        UserShortDto bookerDto = new UserShortDto(booker.getId(), booker.getName());
        ItemShortDto itemDto = new ItemShortDto(item.getId(), item.getName());

        return BookingMapper.toBookingResponseDto(booking, bookerDto, itemDto);
    }

    @Override
    public List<BookingResponseDto> getBookingByUser(Long userId, State state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        // Используем переданные параметры пагинации или значения по умолчанию
        int page = from != null ? from / size : 0;
        int pageSize = size != null ? size : 20;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBooker(userId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBooker(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBooker(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return mapBookingsToResponseDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getBookingByUserItem(Long userId, State state, Integer from, Integer size) { // добавлена пагинация
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        List<Item> userItems = itemRepository.findByUserId(userId);

        if (userItems.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = userItems.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        // Используем переданные параметры пагинации или значения по умолчанию
        int page = from != null ? from / size : 0;
        int pageSize = size != null ? size : 20;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemIdIn(itemIds, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwner(itemIds, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwner(itemIds, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwner(itemIds, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemIdInAndStatus(itemIds, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemIdInAndStatus(itemIds, Status.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return mapBookingsToResponseDto(bookings);
    }

    private List<BookingResponseDto> mapBookingsToResponseDto(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new NotFoundException("Item not found"));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new NotFoundException("Booker not found"));
                    UserShortDto bookerDto = new UserShortDto(booker.getId(), booker.getName());
                    ItemShortDto itemDto = new ItemShortDto(item.getId(), item.getName());
                    return BookingMapper.toBookingResponseDto(booking, bookerDto, itemDto);
                })
                .collect(Collectors.toList());
    }

    private void validateBooking(BookingRequestDto bookingRequestDto, Item item, Long userId) {
        if (item.getUserId().equals(userId)) {
            throw new NotFoundException("Cannot book your own item");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        LocalDateTime now = LocalDateTime.now();
        if (bookingRequestDto.getStart() == null) {
            throw new ValidationException("Start date is required");
        }
        if (bookingRequestDto.getEnd() == null) {
            throw new ValidationException("End date is required");
        }
        if (bookingRequestDto.getStart().isBefore(now)) {
            throw new ValidationException("Start date cannot be in the past");
        }
        if (bookingRequestDto.getEnd().isBefore(now)) {
            throw new ValidationException("End date cannot be in the past");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Start date must be before end date");
        }
        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new ValidationException("Start and end dates cannot be equal");
        }
    }
}