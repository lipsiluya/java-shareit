package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void create_WithValidData_CreatesBooking() {
        // Создаем пользователей с помощью репозитория
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        User savedBooker = userRepository.save(booker);

        // Создаем предмет
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setUserId(savedOwner.getId());
        Item savedItem = itemRepository.save(item);

        // Создаем DTO для бронирования
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        // Вызываем сервис
        BookingResponseDto result = bookingService.create(savedBooker.getId(), bookingDto);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(savedItem.getId(), result.getItem().getId());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void getBookingByUser_WithValidUser_ReturnsBookings() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        User savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setUserId(savedOwner.getId());
        Item savedItem = itemRepository.save(item);

        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(savedBooker.getId(), bookingDto);

        List<BookingResponseDto> bookings = bookingService.getBookingByUser(
                savedBooker.getId(), State.ALL, 0, 10
        );

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }
}