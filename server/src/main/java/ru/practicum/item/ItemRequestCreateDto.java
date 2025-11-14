package ru.practicum.item;

import lombok.Data;

@Data
public class ItemRequestCreateDto {
    private String description; // Убрал @NotBlank
}