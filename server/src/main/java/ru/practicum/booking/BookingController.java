package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) { // Убрал @Valid
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                      @PathVariable("bookingId") Long bookingId,
                                      @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                  @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                                    @RequestParam(required = false, defaultValue = "ALL") State state,
                                                    @RequestParam(required = false) Integer from,
                                                    @RequestParam(required = false) Integer size) {
        return bookingService.getBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") State state,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size) {
        return bookingService.getBookingByUserItem(userId, state, from, size);
    }
}