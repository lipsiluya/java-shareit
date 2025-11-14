package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_WithValidData_CreatesUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@mail.com");

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void getAll_ReturnsAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("User 1");
        user1.setEmail("user1@mail.com");
        userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("User 2");
        user2.setEmail("user2@mail.com");
        userService.create(user2);

        List<UserDto> users = userService.getAll();

        assertTrue(users.size() >= 2);
    }
}