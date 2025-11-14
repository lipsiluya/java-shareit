package ru.practicum.item;

import lombok.Data;
import ru.practicum.booking.BookingShortDto;
import ru.practicum.item.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;

    private List<CommentDto> comments = new ArrayList<>();
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}