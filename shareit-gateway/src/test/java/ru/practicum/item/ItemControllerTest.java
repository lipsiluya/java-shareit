package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.client.ItemClient;
import ru.practicum.item.comment.CommentDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_WithValidData_ReturnsOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful electric drill");
        itemDto.setAvailable(true);

        Mockito.when(itemClient.createItem(anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Drill\"}", HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_WithoutUserId_ReturnsBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithValidData_ReturnsOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Drill");
        itemDto.setDescription("Updated description"); // Добавить описание
        itemDto.setAvailable(true); // Добавить available

        Mockito.when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Updated Drill\"}", HttpStatus.OK));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void get_WithItemId_ReturnsOk() throws Exception {
        Mockito.when(itemClient.getItem(anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Drill\"}", HttpStatus.OK));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_WithUserId_ReturnsOk() throws Exception {
        Mockito.when(itemClient.getUserItems(anyLong()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"name\":\"Drill\"}]", HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void search_WithText_ReturnsOk() throws Exception {
        Mockito.when(itemClient.searchItems(anyString()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"name\":\"Drill\"}]", HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_WithValidData_ReturnsOk() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Mockito.when(itemClient.addComment(anyLong(), anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"text\":\"Great item!\"}", HttpStatus.OK));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}