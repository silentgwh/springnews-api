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

/**
 * REST-контроллер для управления комментариями.
 *
 * Предоставляет базовые CRUD-операции:
 * - Получение всех комментариев
 * - Получение комментария по ID
 * - Получение комментариев, связанных с конкретной новостью
 * - Создание нового комментария
 * - Обновление существующего комментария
 * - Удаление комментария
 *
 * Все методы логируются: info — для действий, debug — для payload'ов.
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Получает список всех комментариев.
     *
     * @return список DTO комментариев
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        log.info("GET /api/comments — fetch all comments");
        return ResponseEntity.ok(commentService.getAllComments());
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param id идентификатор комментария
     * @return DTO комментария
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        log.info("GET /api/comments/{} — fetch comment by ID", id);
        return ResponseEntity.ok(commentService.getComment(id));
    }

    /**
     * Получает все комментарии, связанные с конкретной новостью.
     *
     * @param newsId идентификатор новости
     * @return список DTO комментариев
     */
    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentDto>> getCommentsByNews(@PathVariable Long newsId) {
        log.info("GET /api/comments/news/{} — fetch comments for news", newsId);
        return ResponseEntity.ok(commentService.getCommentsByNews(newsId));
    }

    /**
     * Создаёт новый комментарий.
     *
     * @param dto DTO с данными комментария
     * @return созданный комментарий
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto dto) {
        log.info("POST /api/comments — create comment");
        log.debug("Payload: {}", dto);
        CommentDto created = commentService.createComment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновляет существующий комментарий.
     *
     * @param id идентификатор комментария
     * @param dto DTO с обновлёнными данными
     * @return обновлённый комментарий
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @RequestBody @Valid CommentDto dto) {
        log.info("PUT /api/comments/{} — update comment", id);
        log.debug("Payload: {}", dto);
        return ResponseEntity.ok(commentService.updateComment(id, dto));
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param id идентификатор комментария
     * @return пустой ответ с кодом 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("DELETE /api/comments/{} — delete comment", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}