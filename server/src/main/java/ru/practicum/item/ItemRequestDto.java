package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Request description cannot be blank")
    private String description;

    private LocalDateTime created;
    private List<ItemResponseDto> items;
}