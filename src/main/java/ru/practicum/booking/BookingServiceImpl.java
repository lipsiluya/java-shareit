package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId) {
        // Проверяем пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Проверяем предмет
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // Валидация
        validateBooking(bookingRequestDto, item, userId);

        // Создаем бронирование
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItemId(item.getId());
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        // Маппинг в ResponseDto
        UserShortDto bookerDto = new UserShortDto(booker.getId(), booker.getName());
        ItemShortDto itemDto = new ItemShortDto(item.getId(), item.getName());

        return BookingMapper.toBookingResponseDto(savedBooking, bookerDto, itemDto);
    }

    private void validateBooking(BookingRequestDto bookingRequestDto, Item item, Long userId) {
        // Проверка владельца
        if (item.getUserId().equals(userId)) {
            throw new NotFoundException("Cannot book your own item");
        }

        // Проверка доступности
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        // Валидация дат
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

    @Override
    public BookingResponseDto approved(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Получаем предмет бронирования
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // используем ForbiddenException вместо NotFoundException
        if (!item.getUserId().equals(ownerId)) {
            throw new ForbiddenException("Only item owner can approve booking"); // Теперь вернет 403
        }

        // Проверяем текущий статус
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Booking status cannot be changed");
        }

        // Обновляем статус
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Маппинг в ResponseDto
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

        // Получаем предмет и бронирующего
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker not found"));

        // Проверяем права доступа
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
    public List<BookingResponseDto> getBookingByUser(Long userId, State state) {
        // Проверяем пользователя
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("User not found");
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId);
                break;
            case CURRENT:
                // В текущей реализации нет отдельного метода для CURRENT, поэтому используем ALL и фильтруем
                bookings = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerId(userId).stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

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

    @Override
    public List<BookingResponseDto> getBookingByUserItem(Long userId, State state) {
        // Проверяем пользователя
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("User not found");
        }

        // Получаем все предметы пользователя
        List<Item> userItems = itemRepository.findByUserId(userId);

        // Получаем все бронирования для этих предметов
        List<Booking> bookings = userItems.stream()
                .flatMap(item -> bookingRepository.findByItemId(item.getId()).stream())
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        // Фильтруем по state
        switch (state) {
            case ALL:
                break;
            case CURRENT:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

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
}