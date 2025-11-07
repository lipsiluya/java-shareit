package ru.practicum.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private final Map<Long, Booking> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idGenerator.getAndIncrement());
        }
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return storage.values().stream()
                .filter(booking -> Objects.equals(booking.getBookerId(), bookerId))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemId(Long itemId) {
        return storage.values().stream()
                .filter(booking -> Objects.equals(booking.getItemId(), itemId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByBookerIdAndStatus(Long bookerId, Status status) {
        return storage.values().stream()
                .filter(booking -> Objects.equals(booking.getBookerId(), bookerId))
                .filter(booking -> booking.getStatus() == status)
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemOwnerId(Long ownerId) {
        return storage.values().stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemOwnerIdAndStatus(Long ownerId, Status status) {
        // Аналогично предыдущему методу
        return storage.values().stream()
                .filter(booking -> booking.getStatus() == status)
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now) {
        return storage.values().stream()
                .filter(booking -> Objects.equals(booking.getBookerId(), bookerId))
                .filter(booking -> Objects.equals(booking.getItemId(), itemId))
                .filter(booking -> booking.getEnd().isBefore(now))
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .collect(Collectors.toList());
    }
}