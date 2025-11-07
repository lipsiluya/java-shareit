package ru.practicum.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto approved(Long bookingId, Long ownerId, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingByUser(Long userId, State state);

    List<BookingResponseDto> getBookingByUserItem(Long userId, State state);
}