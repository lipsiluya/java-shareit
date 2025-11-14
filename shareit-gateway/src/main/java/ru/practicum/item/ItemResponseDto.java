package ru.practicum.item;

import lombok.Data;
import ru.practicum.booking.BookingShortDto;
import ru.practicum.item.comment.CommentDto;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}