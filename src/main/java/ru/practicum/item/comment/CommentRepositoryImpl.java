package ru.practicum.item.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final Map<Long, Comment> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(idGenerator.getAndIncrement());
        }
        storage.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Comment> findByItemId(Long itemId) {
        return storage.values().stream()
                .filter(comment -> Objects.equals(comment.getItemId(), itemId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(comment -> Objects.equals(comment.getAuthorId(), authorId))
                .collect(Collectors.toList());
    }
}