package by.mosquitto.mapper;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.entity.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .creationDate(comment.getCreationDate())
                .lastEditDate(comment.getLastEditDate())
                .newsId(comment.getNews().getId())
                .userId(comment.getCreatedByUser().getId())
                .build();
    }
}