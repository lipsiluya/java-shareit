package ru.practicum.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.item.ItemShortDto;
import ru.practicum.user.UserShortDto;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking, UserShortDto booker, ItemShortDto item) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booker)
                .item(item)
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}