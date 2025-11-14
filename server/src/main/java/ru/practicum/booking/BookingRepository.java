package ru.practicum.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Базовые методы
    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemIdIn(List<Long> itemIds, Pageable pageable);

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, Status status, Pageable pageable);

    List<Booking> findByItemIdIn(List<Long> itemIds);

    // Для booker с фильтрами по времени
    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.start < :now AND b.end > :now")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.end < :now")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.start > :now")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable pageable);

    // Для owner с фильтрами по времени
    @Query("SELECT b FROM Booking b WHERE b.itemId IN :itemIds AND b.start < :now AND b.end > :now")
    List<Booking> findCurrentBookingsByOwner(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN :itemIds AND b.end < :now")
    List<Booking> findPastBookingsByOwner(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN :itemIds AND b.start > :now")
    List<Booking> findFutureBookingsByOwner(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now, Pageable pageable);

    // Для комментариев
    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    List<Booking> findByItemId(Long itemId);
}