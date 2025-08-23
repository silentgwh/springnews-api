package by.mosquitto.service.contract;

import by.mosquitto.dto.NewsDto;

import java.util.List;

public interface NewsService {
    NewsDto createNews(NewsDto dto);
    NewsDto getNews(Long id);
    List<NewsDto> getAllNews();
    NewsDto updateNews(Long id, NewsDto dto);
    void deleteNews(Long id);
}
