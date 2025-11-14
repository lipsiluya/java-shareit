package ru.practicum.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto); // userId первым параметром

    BookingResponseDto approve(Long bookingId, Long ownerId, Boolean approved); // исправленное название метода

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingByUser(Long userId, State state, Integer from, Integer size); // добавлены пагинация

    List<BookingResponseDto> getBookingByUserItem(Long userId, State state, Integer from, Integer size); // добавлены пагинация
}