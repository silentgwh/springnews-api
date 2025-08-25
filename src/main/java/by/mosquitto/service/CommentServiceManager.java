package by.mosquitto.service;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.entity.Comment;
import by.mosquitto.entity.News;
import by.mosquitto.entity.User;
import by.mosquitto.exception.CommentNotFoundException;
import by.mosquitto.exception.NewsNotFoundException;
import by.mosquitto.exception.UserNotFoundException;
import by.mosquitto.mapper.CommentMapper;
import by.mosquitto.repository.CommentRepository;
import by.mosquitto.repository.NewsRepository;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceManager implements CommentService {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDto> getAllComments() {
        log.debug("Fetching all comments");
        return commentRepository.findAll().stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto dto) {
        log.info("Creating comment for newsId={}, userId={}", dto.getNewsId(), dto.getUserId());
        log.debug("Payload: {}", dto);

        News news = newsRepository.findById(dto.getNewsId())
                .orElseThrow(() -> {
                    log.warn("News not found: id={}", dto.getNewsId());
                    return new NewsNotFoundException(dto.getNewsId());
                });

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", dto.getUserId());
                    return new UserNotFoundException(dto.getUserId());
                });

        Comment comment = Comment.builder()
                .text(dto.getText())
                .creationDate(LocalDateTime.now())
                .news(news)
                .createdByUser(user)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comment created: id={}", saved.getId());
        return CommentMapper.toDto(saved);
    }

    @Override
    public CommentDto getComment(Long id) {
        log.info("Fetching comment by id={}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found: id={}", id);
                    return new CommentNotFoundException(id);
                });
        return CommentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByNews(Long newsId) {
        log.info("Fetching comments for newsId={}", newsId);
        return commentRepository.findByNewsId(newsId).stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, CommentDto dto) {
        log.info("Updating comment id={}", id);
        log.debug("Payload: {}", dto);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found for update: id={}", id);
                    return new CommentNotFoundException(id);
                });

        comment.setText(dto.getText());
        comment.setLastEditDate(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        log.info("Comment updated: id={}", updated.getId());
        return CommentMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        log.info("Deleting comment id={}", id);
        if (!commentRepository.existsById(id)) {
            log.warn("Comment not found for deletion: id={}", id);
            throw new CommentNotFoundException(id);
        }
        commentRepository.deleteById(id);
        log.info("Comment deleted: id={}", id);
    }
}