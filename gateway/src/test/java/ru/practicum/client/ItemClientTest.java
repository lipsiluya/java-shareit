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
import ru.practicum.item.ItemDto;
import ru.practicum.item.comment.CommentDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:9090", restTemplate);
    }

    @Test
    void createItem_ShouldCallPostWithCorrectUrl() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");

        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.createItem(1L, itemDto);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/items"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<String> capturedEntity = httpEntityCaptor.getValue();
        assertNotNull(capturedEntity.getHeaders().get("X-Sharer-User-Id"));
        assertEquals("1", capturedEntity.getHeaders().get("X-Sharer-User-Id").get(0));
    }

    @Test
    void updateItem_ShouldCallPatchWithCorrectUrl() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Drill");

        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.updateItem(1L, 2L, itemDto);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/items/2"),
                eq(HttpMethod.PATCH),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }

    @Test
    void searchItems_ShouldCallGetWithCorrectUrl() {
        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.searchItems("drill");

        verify(restTemplate).exchange(
                eq("http://localhost:9090/items/search?text=drill"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }

    @Test
    void addComment_ShouldCallPostWithCorrectUrl() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        ResponseEntity<Object> response = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        itemClient.addComment(1L, 2L, commentDto);

        verify(restTemplate).exchange(
                eq("http://localhost:9090/items/2/comment"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );
    }
}