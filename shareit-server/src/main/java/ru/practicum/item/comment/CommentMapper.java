package ru.practicum.item.comment;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment, String authorName) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(authorName)
                .created(comment.getCreated())
                .build();
    }
}