package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Request description cannot be blank")
    private String description;
}