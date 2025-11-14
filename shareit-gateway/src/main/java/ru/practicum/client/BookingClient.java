package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.booking.BookingRequestDto;
import ru.practicum.booking.State;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private final String serverUrl;

    @Autowired
    public BookingClient(@Value("${shareit.server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        String url = serverUrl + API_PREFIX;
        return post(url, userId, bookingRequestDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, Boolean approved) {
        String url = serverUrl + API_PREFIX + "/" + bookingId + "?approved=" + approved;
        return patch(url, userId); // PATCH без body
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        String url = serverUrl + API_PREFIX + "/" + bookingId;
        return get(url, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, State state, Integer from, Integer size) {
        String url = serverUrl + API_PREFIX;

        Map<String, Object> parameters = new HashMap<>();
        if (state != null) parameters.put("state", state.name());
        if (from != null) parameters.put("from", from);
        if (size != null) parameters.put("size", size);

        return get(url, userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, State state, Integer from, Integer size) {
        String url = serverUrl + API_PREFIX + "/owner";

        Map<String, Object> parameters = new HashMap<>();
        if (state != null) parameters.put("state", state.name());
        if (from != null) parameters.put("from", from);
        if (size != null) parameters.put("size", size);

        return get(url, userId, parameters);
    }
}