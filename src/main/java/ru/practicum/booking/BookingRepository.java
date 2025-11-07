package ru.practicum.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status);

    List<Booking> findByItemOwnerId(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, Status status);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, java.time.LocalDateTime now);
}