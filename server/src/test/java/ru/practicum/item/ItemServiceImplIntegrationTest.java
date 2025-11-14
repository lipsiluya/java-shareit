package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getByOwner_WithValidOwner_ReturnsItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        User savedOwner = userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");
        itemDto.setAvailable(true);

        ItemDto createdItem = itemService.create(savedOwner.getId(), itemDto);
        List<ItemDto> userItems = itemService.getByOwner(savedOwner.getId());

        assertNotNull(userItems);
        assertEquals(1, userItems.size());
        assertEquals("Item 1", userItems.get(0).getName());
    }

    @Test
    void search_WithExistingText_ReturnsMatchingItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        User savedOwner = userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful electric drill");
        itemDto.setAvailable(true);

        itemService.create(savedOwner.getId(), itemDto);

        List<ItemDto> foundItems = itemService.search("drill");

        assertFalse(foundItems.isEmpty());
        assertTrue(foundItems.stream().anyMatch(item ->
                item.getName().toLowerCase().contains("drill") ||
                        item.getDescription().toLowerCase().contains("drill")
        ));
    }
}