package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item description cannot be blank")
    private String description;

    @NotNull(message = "Available status must be specified")
    private Boolean available;
}