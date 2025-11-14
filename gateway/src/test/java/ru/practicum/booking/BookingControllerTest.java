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
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(1L);

        Mockito.when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"status\":\"WAITING\"}", HttpStatus.OK));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
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