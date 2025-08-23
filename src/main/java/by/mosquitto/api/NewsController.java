package by.mosquitto.api;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.service.contract.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsDto>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @PostMapping
    public ResponseEntity<NewsDto> create(@RequestBody NewsDto dto) {
        return ResponseEntity.ok(newsService.createNews(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNews(id));
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
