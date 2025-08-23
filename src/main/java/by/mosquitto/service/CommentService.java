package by.mosquitto.service;

import by.mosquitto.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(CommentDto dto);
    CommentDto getComment(Long id);
    List<CommentDto> getCommentsByNews(Long newsId);
    CommentDto updateComment(Long id, CommentDto dto);
    void deleteComment(Long id);
}
