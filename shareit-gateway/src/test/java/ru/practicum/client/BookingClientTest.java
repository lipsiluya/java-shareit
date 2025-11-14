package ru.practicum.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.booking.State;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:9090", restTemplate);
    }

    @Test
    void getBooking_ShouldCallGetWithCorrectUrl() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyLong()))
                .thenReturn(response);

        bookingClient.getBooking(1L, 2L);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/bookings/{bookingId}"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class),
                eq(2L)
        );
    }

    @Test
    void getUserBookings_WithStateAndPagination_ShouldCallGet() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.getUserBookings(1L, State.ALL, 0, 10);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getOwnerBookings_WithStateAndPagination_ShouldCallGet() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.getOwnerBookings(1L, State.CURRENT, 5, 20);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getUserBookings_WithoutParameters_ShouldCallGet() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        bookingClient.getUserBookings(1L, null, null, null);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }
}