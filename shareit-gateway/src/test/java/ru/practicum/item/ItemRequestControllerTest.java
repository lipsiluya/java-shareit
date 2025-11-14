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
import ru.practicum.client.ItemRequestClient;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void create_WithValidData_ReturnsOk() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        Mockito.when(itemRequestClient.createRequest(anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"description\":\"Need a power drill\"}", HttpStatus.OK));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_WithoutUserId_ReturnsBadRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_WithValidUser_ReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.getUserRequests(anyLong()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"description\":\"Need drill\"}]", HttpStatus.OK));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOtherUsersRequests_WithPagination_ReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.getOtherUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"description\":\"Need drill\"}]", HttpStatus.OK));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getOtherUsersRequests_WithoutPagination_ReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.getOtherUsersRequests(anyLong(), isNull(), isNull()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"description\":\"Need drill\"}]", HttpStatus.OK));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_WithValidIds_ReturnsOk() throws Exception {
        Mockito.when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"description\":\"Need drill\"}", HttpStatus.OK));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}