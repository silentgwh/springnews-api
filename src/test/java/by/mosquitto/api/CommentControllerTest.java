package by.mosquitto.api;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.service.contract.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentDto comment;

    @BeforeEach
    void setUp() {
        comment = CommentDto.builder()
                .id(1L)
                .text("Nice article")
                .creationDate(LocalDateTime.now().minusHours(1))
                .lastEditDate(LocalDateTime.now())
                .userId(100L)
                .newsId(200L)
                .build();
    }

    @Test
    void getAllComments_shouldReturnListOfComments() {
        List<CommentDto> comments = List.of(comment);
        Mockito.when(commentService.getAllComments()).thenReturn(comments);

        ResponseEntity<List<CommentDto>> response = commentController.getAllComments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    void getComment_shouldReturnCommentById() {
        Mockito.when(commentService.getComment(1L)).thenReturn(comment);

        ResponseEntity<CommentDto> response = commentController.getComment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void getCommentsByNews_shouldReturnCommentsForNews() {
        List<CommentDto> comments = List.of(comment);
        Mockito.when(commentService.getCommentsByNews(200L)).thenReturn(comments);

        ResponseEntity<List<CommentDto>> response = commentController.getCommentsByNews(200L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    void createComment_shouldReturnCreatedComment() {
        Mockito.when(commentService.createComment(comment)).thenReturn(comment);

        ResponseEntity<CommentDto> response = commentController.createComment(comment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() {
        Mockito.when(commentService.updateComment(1L, comment)).thenReturn(comment);

        ResponseEntity<CommentDto> response = commentController.updateComment(1L, comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    void deleteComment_shouldReturnNoContent() {
        Mockito.doNothing().when(commentService).deleteComment(1L);

        ResponseEntity<Void> response = commentController.deleteComment(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
