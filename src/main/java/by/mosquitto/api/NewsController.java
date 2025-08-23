package by.mosquitto.api;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ResponseEntity<NewsDto> create(@RequestBody NewsDto dto) {
        return ResponseEntity.ok(newsService.createNews(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNews(id));
    }

    @GetMapping
    public ResponseEntity<List<NewsDto>> getAll() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsDto> update(@PathVariable Long id, @RequestBody NewsDto dto) {
        return ResponseEntity.ok(newsService.updateNews(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
