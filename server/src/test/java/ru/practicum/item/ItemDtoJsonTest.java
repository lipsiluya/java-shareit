package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Powerful electric drill");
        dto.setAvailable(true);
        dto.setRequestId(5L);

        String jsonContent = json.write(dto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"name\":\"Drill\"");
        assertThat(jsonContent).contains("\"description\":\"Powerful electric drill\"");
        assertThat(jsonContent).contains("\"available\":true");
        assertThat(jsonContent).contains("\"requestId\":5");
    }

    @Test
    void shouldDeserializeItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful electric drill\",\"available\":true,\"requestId\":5}";

        ItemDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful electric drill");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getRequestId()).isEqualTo(5L);
    }
}