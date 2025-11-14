package ru.practicum.item.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.item.ItemRequestDto;
import ru.practicum.item.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeItemRequestDto() throws Exception {
        ItemResponseDto itemResponse = new ItemResponseDto(1L, "Drill", 2L, "Powerful drill", true, 1L);
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a power drill");
        dto.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));
        dto.setItems(List.of(itemResponse));

        String jsonContent = json.write(dto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"description\":\"Need a power drill\"");
        assertThat(jsonContent).contains("\"created\":\"2024-01-01T10:00:00\"");
        assertThat(jsonContent).contains("\"items\"");
    }

    @Test
    void shouldDeserializeItemRequestDto() throws Exception {
        String content = "{\"id\":1,\"description\":\"Need a power drill\",\"created\":\"2024-01-01T10:00:00\"}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a power drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }
}