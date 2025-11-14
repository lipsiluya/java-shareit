package ru.practicum.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findOtherUsersRequests(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findOtherUsersRequests(@Param("userId") Long userId);
}