package by.mosquitto.service;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.dto.NewsDto;
import by.mosquitto.dto.NewsWithCommentsPagedDto;
import by.mosquitto.entity.News;
import by.mosquitto.entity.User;
import by.mosquitto.exception.NewsNotFoundException;
import by.mosquitto.exception.UserNotFoundException;
import by.mosquitto.mapper.CommentMapper;
import by.mosquitto.mapper.NewsMapper;
import by.mosquitto.repository.CommentRepository;
import by.mosquitto.repository.NewsRepository;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceManager implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<NewsDto> getAllNews() {
        return newsRepository.findAll().stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    @Override
    public Page<NewsDto> getNewsPaged(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(NewsMapper::toDto);
    }

    @Override
    public NewsWithCommentsPagedDto getNewsWithCommentsPaged(Long newsId, Pageable pageable) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));

        Page<CommentDto> comments = commentRepository.findByNewsId(newsId, pageable)
                .map(CommentMapper::toDto);

        return NewsWithCommentsPagedDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .text(news.getText())
                .creationDate(news.getCreationDate())
                .lastEditDate(news.getLastEditDate())
                .insertedById(news.getCreatedByUser().getId())
                .updatedById(news.getUpdatedByUser() != null ? news.getUpdatedByUser().getId() : null)
                .comments(comments)
                .build();
    }

    @Override
    public List<NewsDto> search(String query) {
        return newsRepository.searchByTitleOrText(query).stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    @Override
    public NewsDto getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));
        return NewsMapper.toDto(news);
    }

    @Override
    @Transactional
    public NewsDto createNews(NewsDto dto) {
        User createdByUser = userRepository.findById(dto.getInsertedById())
                .orElseThrow(() -> new UserNotFoundException(dto.getInsertedById()));

        News news = News.builder()
                .title(dto.getTitle())
                .text(dto.getText())
                .creationDate(LocalDateTime.now())
                .createdByUser(createdByUser)
                .build();

        return NewsMapper.toDto(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsDto updateNews(Long id, NewsDto dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));

        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setLastEditDate(LocalDateTime.now());

        if (dto.getUpdatedById() != null) {
            User updatedByUser = userRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> new UserNotFoundException(dto.getUpdatedById()));
            news.setUpdatedByUser(updatedByUser);
        }

        return NewsMapper.toDto(newsRepository.save(news));
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NewsNotFoundException(id);
        }
        newsRepository.deleteById(id);
    }
}
