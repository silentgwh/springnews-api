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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис управления новостями.
 *
 * Реализует:
 * - Получение всех новостей (в том числе с пагинацией)
 * - Поиск по заголовку и тексту
 * - Получение новости по ID
 * - Получение новости с постраничными комментариями
 * - Создание, обновление и удаление новости
 *
 * Особенности:
 * - Проверка существования пользователя при создании/обновлении
 * - Обработка ошибок через кастомные исключения (NewsNotFoundException, UserNotFoundException)
 * - Логирование: debug — для payload'ов, info — для действий, warn — при ошибках
 * - Используется @Transactional для операций записи
 * - Возврат DTO через мапперы, без утечек сущностей
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceManager implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * Получает список всех новостей.
     *
     * @return список DTO новостей
     */
    @Override
    public List<NewsDto> getAllNews() {
        log.debug("Fetching all news");
        return newsRepository.findAll().stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    /**
     * Получает новости с пагинацией.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница DTO новостей
     */
    @Override
    public Page<NewsDto> getNewsPaged(Pageable pageable) {
        log.debug("Fetching paged news: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return newsRepository.findAll(pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Получает новость с постраничными комментариями.
     *
     * @param newsId идентификатор новости
     * @param pageable параметры пагинации комментариев
     * @return DTO новости с комментариями
     * @throws NewsNotFoundException если новость не найдена
     */
    @Override
    public NewsWithCommentsPagedDto getNewsWithCommentsPaged(Long newsId, Pageable pageable) {
        log.info("Fetching news with comments: newsId={}, page={}, size={}", newsId, pageable.getPageNumber(), pageable.getPageSize());

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> {
                    log.warn("News not found: id={}", newsId);
                    return new NewsNotFoundException(newsId);
                });

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

    /**
     * Выполняет поиск новостей по заголовку или тексту.
     *
     * @param query поисковый запрос
     * @return список подходящих DTO новостей
     */
    @Override
    public List<NewsDto> search(String query) {
        log.info("Searching news by query='{}'", query);
        return newsRepository.searchByTitleOrText(query).stream()
                .map(NewsMapper::toDto)
                .toList();
    }

    /**
     * Получает новость по её идентификатору.
     *
     * @param id идентификатор новости
     * @return DTO новости
     * @throws NewsNotFoundException если новость не найдена
     */
    @Override
    public NewsDto getNewsById(Long id) {
        log.info("Fetching news by id={}", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("News not found: id={}", id);
                    return new NewsNotFoundException(id);
                });
        return NewsMapper.toDto(news);
    }

    /**
     * Создаёт новую новость.
     *
     * @param dto DTO с данными новости
     * @return созданная новость
     * @throws UserNotFoundException если автор не найден
     */
    @Override
    @Transactional
    public NewsDto createNews(NewsDto dto) {
        log.info("Creating news by userId={}", dto.getInsertedById());
        log.debug("Payload: {}", dto);

        User createdByUser = userRepository.findById(dto.getInsertedById())
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", dto.getInsertedById());
                    return new UserNotFoundException(dto.getInsertedById());
                });

        News news = News.builder()
                .title(dto.getTitle())
                .text(dto.getText())
                .creationDate(LocalDateTime.now())
                .createdByUser(createdByUser)
                .build();

        News saved = newsRepository.save(news);
        log.info("News created: id={}", saved.getId());
        return NewsMapper.toDto(saved);
    }

    /**
     * Обновляет существующую новость.
     *
     * @param id идентификатор новости
     * @param dto DTO с обновлёнными данными
     * @return обновлённая новость
     * @throws NewsNotFoundException если новость не найдена
     * @throws UserNotFoundException если указанный обновляющий пользователь не найден
     */
    @Override
    @Transactional
    public NewsDto updateNews(Long id, NewsDto dto) {
        log.info("Updating news id={}", id);
        log.debug("Payload: {}", dto);

        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("News not found for update: id={}", id);
                    return new NewsNotFoundException(id);
                });

        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setLastEditDate(LocalDateTime.now());

        if (dto.getUpdatedById() != null) {
            User updatedByUser = userRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> {
                        log.warn("User not found for update: id={}", dto.getUpdatedById());
                        return new UserNotFoundException(dto.getUpdatedById());
                    });
            news.setUpdatedByUser(updatedByUser);
        }

        News updated = newsRepository.save(news);
        log.info("News updated: id={}", updated.getId());
        return NewsMapper.toDto(updated);
    }

    /**
     * Удаляет новость по её идентификатору.
     *
     * @param id идентификатор новости
     * @throws NewsNotFoundException если новость не найдена
     */
    @Override
    @Transactional
    public void deleteNews(Long id) {
        log.info("Deleting news id={}", id);
        if (!newsRepository.existsById(id)) {
            log.warn("News not found for deletion: id={}", id);
            throw new NewsNotFoundException(id);
        }
        newsRepository.deleteById(id);
        log.info("News deleted: id={}", id);
    }
}