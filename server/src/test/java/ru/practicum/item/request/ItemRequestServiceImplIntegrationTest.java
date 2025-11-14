package ru.practicum.item.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.ItemRequestCreateDto;
import ru.practicum.item.ItemRequestDto;
import ru.practicum.item.ItemRequestService;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_WithValidData_CreatesRequest() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");
        User savedUser = userRepository.save(user);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        ItemRequestDto result = itemRequestService.create(savedUser.getId(), createDto);

        assertNotNull(result);
        assertEquals("Need a power drill", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void getUserRequests_WithExistingRequests_ReturnsRequests() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");
        User savedUser = userRepository.save(user);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill");

        itemRequestService.create(savedUser.getId(), createDto);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(savedUser.getId());

        assertFalse(requests.isEmpty());
        assertEquals("Need a power drill", requests.get(0).getDescription());
    }
}