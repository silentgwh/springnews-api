package by.mosquitto.service;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.entity.Comment;
import by.mosquitto.entity.News;
import by.mosquitto.entity.User;
import by.mosquitto.mapper.CommentMapper;
import by.mosquitto.repository.CommentRepository;
import by.mosquitto.repository.NewsRepository;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static by.mosquitto.mapper.CommentMapper.toDto;

@Service
@RequiredArgsConstructor
public class CommentServiceManager implements CommentService {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto dto) {
        News news = newsRepository.findById(dto.getNewsId())
                .orElseThrow(() -> new RuntimeException("News not found: " + dto.getNewsId()));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));

        Comment comment = Comment.builder()
                .text(dto.getText())
                .creationDate(LocalDateTime.now())
                .news(news)
                .createdByUser(user)
                .build();

        return toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        return toDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByNews(Long newsId) {
        return commentRepository.findByNewsId(newsId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, CommentDto dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + id));

        comment.setText(dto.getText());
        comment.setLastEditDate(LocalDateTime.now());

        return toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found: " + id);
        }
        commentRepository.deleteById(id);
    }
}