package ru.practicum.booking;

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
import ru.practicum.client.BookingClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void create_WithValidData_ReturnsOk() throws Exception {
        // Используем валидные даты вместо null
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void approve_WithValidData_ReturnsOk() throws Exception {
        Mockito.when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"status\":\"APPROVED\"}", HttpStatus.OK));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void get_WithValidIds_ReturnsOk() throws Exception {
        Mockito.when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"status\":\"APPROVED\"}", HttpStatus.OK));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_WithState_ReturnsOk() throws Exception {
        Mockito.when(bookingClient.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"status\":\"APPROVED\"}]", HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_WithState_ReturnsOk() throws Exception {
        Mockito.when(bookingClient.getOwnerBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"status\":\"APPROVED\"}]", HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}