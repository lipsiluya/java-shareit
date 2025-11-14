package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.exception.ValidationException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                         @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam Boolean approved) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                      @PathVariable("bookingId") Long bookingId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") State state,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(value = USER_HEADER, required = false) Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is required");
        }
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}