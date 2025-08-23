package by.mosquitto.service.contract;

import by.mosquitto.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllComments();
    CommentDto createComment(CommentDto dto);
    CommentDto getComment(Long id);
    List<CommentDto> getCommentsByNews(Long newsId);
    CommentDto updateComment(Long id, CommentDto dto);
    void deleteComment(Long id);
}
