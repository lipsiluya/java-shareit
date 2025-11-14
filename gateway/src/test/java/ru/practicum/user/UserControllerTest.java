package ru.practicum.user;

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
import ru.practicum.client.UserClient;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_WithValidData_ReturnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@mail.com");

        Mockito.when(userClient.createUser(any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"John Doe\"}", HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void update_WithValidData_ReturnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");

        Mockito.when(userClient.updateUser(anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Updated Name\"}", HttpStatus.OK));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void get_WithUserId_ReturnsOk() throws Exception {
        Mockito.when(userClient.getUser(anyLong()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"John Doe\"}", HttpStatus.OK));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        Mockito.when(userClient.getAllUsers())
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"name\":\"John Doe\"}]", HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_WithUserId_ReturnsOk() throws Exception {
        Mockito.when(userClient.deleteUser(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}