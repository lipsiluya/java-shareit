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
import ru.practicum.item.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:9090", restTemplate);
    }

    @Test
    void createRequest_ShouldCallPostWithCorrectUrl() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.createRequest(1L, createDto);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/requests"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<String> capturedEntity = httpEntityCaptor.getValue();
        assertNotNull(capturedEntity.getHeaders().get("X-Sharer-User-Id"));
        assertEquals("1", capturedEntity.getHeaders().get("X-Sharer-User-Id").get(0));
    }

    @Test
    void getUserRequests_ShouldCallGetWithCorrectUrl() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getUserRequests(1L);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/requests"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }

    @Test
    void getOtherUsersRequests_WithPagination_ShouldCallGetWithCorrectUrl() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getOtherUsersRequests(1L, 0, 10);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/requests/all?from=0&size=10"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }

    @Test
    void getRequestById_ShouldCallGetWithCorrectUrl() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemRequestClient.getRequestById(1L, 2L);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/requests/2"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }
}