package by.mosquitto.api;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.service.contract.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        log.info("GET /api/comments — fetch all comments");
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        log.info("GET /api/comments/{} — fetch comment by ID", id);
        return ResponseEntity.ok(commentService.getComment(id));
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentDto>> getCommentsByNews(@PathVariable Long newsId) {
        log.info("GET /api/comments/news/{} — fetch comments for news", newsId);
        return ResponseEntity.ok(commentService.getCommentsByNews(newsId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto dto) {
        log.info("POST /api/comments — create comment");
        log.debug("Payload: {}", dto);
        CommentDto created = commentService.createComment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @RequestBody @Valid CommentDto dto) {
        log.info("PUT /api/comments/{} — update comment", id);
        log.debug("Payload: {}", dto);
        return ResponseEntity.ok(commentService.updateComment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("DELETE /api/comments/{} — delete comment", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
