package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @Test
    void shouldSerializeItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a power drill");

        String jsonContent = json.write(dto).getJson();

        assertThat(jsonContent).contains("\"description\":\"Need a power drill\"");
        assertThat(jsonContent).doesNotContain("\"id\"");
        assertThat(jsonContent).doesNotContain("\"created\"");
    }

    @Test
    void shouldDeserializeItemRequestCreateDto() throws Exception {
        String content = "{\"description\":\"Need a power drill\"}";

        ItemRequestCreateDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Need a power drill");
    }
}