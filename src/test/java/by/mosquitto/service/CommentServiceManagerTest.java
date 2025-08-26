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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceManagerTest {

    @Mock private CommentRepository commentRepository;
    @Mock private NewsRepository newsRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentServiceManager commentService;

    private Comment comment;
    private CommentDto dto;
    private News news;
    private User user;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();

        news = News.builder().id(1L).title("Title").build();
        user = User.builder().id(2L).username("user").build();

        comment = Comment.builder()
                .id(10L)
                .text("Comment text")
                .creationDate(now.minusDays(1))
                .lastEditDate(now)
                .news(news)
                .createdByUser(user)
                .build();

        dto = CommentDto.builder()
                .id(10L)
                .text("Comment text")
                .creationDate(comment.getCreationDate())
                .lastEditDate(comment.getLastEditDate())
                .newsId(news.getId())
                .userId(user.getId())
                .build();
    }

    @Test
    void getAllComments_shouldReturnMappedList() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));
        try (MockedStatic<CommentMapper> mocked = mockStatic(CommentMapper.class)) {
            mocked.when(() -> CommentMapper.toDto(comment)).thenReturn(dto);

            List<CommentDto> result = commentService.getAllComments();

            assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
        }
    }

    @Test
    void getComment_shouldReturnDto() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        try (MockedStatic<CommentMapper> mocked = mockStatic(CommentMapper.class)) {
            mocked.when(() -> CommentMapper.toDto(comment)).thenReturn(dto);

            CommentDto result = commentService.getComment(10L);

            assertEquals(dto, result);
        }
    }

    @Test
    void getComment_shouldThrowIfNotFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getComment(99L));
    }

    @Test
    void getCommentsByNews_shouldReturnList() {
        when(commentRepository.findByNewsId(1L)).thenReturn(List.of(comment));
        try (MockedStatic<CommentMapper> mocked = mockStatic(CommentMapper.class)) {
            mocked.when(() -> CommentMapper.toDto(comment)).thenReturn(dto);

            List<CommentDto> result = commentService.getCommentsByNews(1L);

            assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
        }
    }

    @Test
    void createComment_shouldSaveAndReturnDto() {
        when(newsRepository.findById(dto.getNewsId())).thenReturn(Optional.of(news));
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        try (MockedStatic<CommentMapper> mocked = mockStatic(CommentMapper.class)) {
            mocked.when(() -> CommentMapper.toDto(comment)).thenReturn(dto);

            CommentDto result = commentService.createComment(dto);

            assertEquals(dto, result);
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Test
    void createComment_shouldThrowIfNewsNotFound() {
        when(newsRepository.findById(dto.getNewsId())).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> commentService.createComment(dto));
    }

    @Test
    void createComment_shouldThrowIfUserNotFound() {
        when(newsRepository.findById(dto.getNewsId())).thenReturn(Optional.of(news));
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.createComment(dto));
    }

    @Test
    void updateComment_shouldModifyAndReturnDto() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        try (MockedStatic<CommentMapper> mocked = mockStatic(CommentMapper.class)) {
            mocked.when(() -> CommentMapper.toDto(comment)).thenReturn(dto);

            CommentDto result = commentService.updateComment(10L, dto);

            assertEquals(dto.getText(), result.getText());
            verify(commentRepository).save(comment);
        }
    }

    @Test
    void updateComment_shouldThrowIfNotFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(99L, dto));
    }

    @Test
    void deleteComment_shouldRemoveIfExists() {
        when(commentRepository.existsById(10L)).thenReturn(true);

        commentService.deleteComment(10L);

        verify(commentRepository).deleteById(10L);
    }

    @Test
    void deleteComment_shouldThrowIfNotFound() {
        when(commentRepository.existsById(99L)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(99L));
    }
}