package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.booking.BookingShortDto;
import ru.practicum.item.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item description cannot be blank")
    private String description;

    @NotNull(message = "Item availability must be specified")
    private Boolean available;

    private Long owner;
    private Long requestId;

    private List<CommentDto> comments = new ArrayList<>();
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}