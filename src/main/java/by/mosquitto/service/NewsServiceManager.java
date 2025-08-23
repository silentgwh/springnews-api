package by.mosquitto.service;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.entity.News;
import by.mosquitto.entity.User;
import by.mosquitto.mapper.NewsMapper;
import by.mosquitto.repository.NewsRepository;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static by.mosquitto.mapper.NewsMapper.toDto;

@Service
@RequiredArgsConstructor
public class NewsServiceManager implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NewsDto createNews(NewsDto dto) {
        User createdByUser = userRepository.findById(dto.getInsertedById())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getInsertedById()));

        News news = News.builder()
                .title(dto.getTitle())
                .text(dto.getText())
                .creationDate(LocalDateTime.now())
                .createdByUser(createdByUser)
                .build();

        return toDto(newsRepository.save(news));
    }

    @Override
    public NewsDto getNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found: " + id));
        return toDto(news);
    }

    @Override
    public List<NewsDto> getAllNews() {
        return newsRepository.findAll().stream()
                .map(NewsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NewsDto updateNews(Long id, NewsDto dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found: " + id));

        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setLastEditDate(LocalDateTime.now());

        if (dto.getUpdatedById() != null) {
            User updatedByUser = userRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUpdatedById()));
            news.setUpdatedByUser(updatedByUser);
        }

        return toDto(newsRepository.save(news));
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("News not found: " + id);
        }
        newsRepository.deleteById(id);
    }
}