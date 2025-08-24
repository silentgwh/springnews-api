package by.mosquitto.service.contract;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.dto.NewsWithCommentsPagedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {
    List<NewsDto> search(String query);
    List<NewsDto> getAllNews();
    Page<NewsDto> getNewsPaged(Pageable pageable);
    NewsDto getNewsById(Long id);
    NewsDto createNews(NewsDto dto);
    NewsDto updateNews(Long id, NewsDto dto);
    void deleteNews(Long id);
    NewsWithCommentsPagedDto getNewsWithCommentsPaged(Long newsId, Pageable pageable);
}
