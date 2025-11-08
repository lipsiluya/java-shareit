package ru.practicum.item.comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 1000)
    private String text;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime created;
}