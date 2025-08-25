package by.mosquitto.api;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.dto.NewsWithCommentsPagedDto;
import by.mosquitto.service.contract.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsDto>> getAllNews() {
        log.info("GET /api/news — fetch all news");
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<NewsDto>> getNewsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/news/paged — page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(newsService.getNewsPaged(pageable));
    }

    @GetMapping("/{id}/with-comments-paged")
    public ResponseEntity<NewsWithCommentsPagedDto> getNewsWithCommentsPaged(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/news/{}/with-comments-paged — page={}, size={}, sortBy={}, direction={}", id, page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(newsService.getNewsWithCommentsPaged(id, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NewsDto>> search(@RequestParam String query) {
        log.info("GET /api/news/search — query='{}'", query);
        return ResponseEntity.ok(newsService.search(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> get(@PathVariable Long id) {
        log.info("GET /api/news/{} — fetch news by ID", id);
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PostMapping
    public ResponseEntity<NewsDto> create(@RequestBody NewsDto dto) {
        log.info("POST /api/news — create news");
        log.debug("Payload: {}", dto);
        return ResponseEntity.status(201).body(newsService.createNews(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsDto> update(@PathVariable Long id, @RequestBody NewsDto dto) {
        log.info("PUT /api/news/{} — update news", id);
        log.debug("Payload: {}", dto);
        return ResponseEntity.ok(newsService.updateNews(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/news/{} — delete news", id);
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
