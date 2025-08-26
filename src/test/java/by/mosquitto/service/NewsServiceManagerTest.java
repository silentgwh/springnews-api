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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceManagerTest {

    @Mock private NewsRepository newsRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks private NewsServiceManager newsService;

    private News news;
    private NewsDto dto;
    private User user;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();

        user = User.builder().id(1L).username("alice").build();

        news = News.builder()
                .id(100L)
                .title("Title")
                .text("Text")
                .creationDate(now.minusDays(1))
                .lastEditDate(now)
                .createdByUser(user)
                .updatedByUser(user)
                .build();

        dto = NewsDto.builder()
                .id(100L)
                .title("Title")
                .text("Text")
                .creationDate(news.getCreationDate())
                .lastEditDate(news.getLastEditDate())
                .insertedById(user.getId())
                .updatedById(user.getId())
                .build();
    }

    @Test
    void getAllNews_shouldReturnMappedList() {
        when(newsRepository.findAll()).thenReturn(List.of(news));
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            List<NewsDto> result = newsService.getAllNews();

            assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
        }
    }

    @Test
    void getNewsPaged_shouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<News> page = new PageImpl<>(List.of(news), pageable, 1);
        when(newsRepository.findAll(pageable)).thenReturn(page);
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            Page<NewsDto> result = newsService.getNewsPaged(pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(dto, result.getContent().get(0));
        }
    }

    @Test
    void getNewsWithCommentsPaged_shouldReturnDtoWithComments() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<CommentDto> commentsPage = new PageImpl<>(List.of(), pageable, 0);

        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));
        when(commentRepository.findByNewsId(100L, pageable)).thenReturn(Page.empty());
        try (MockedStatic<CommentMapper> commentMock = mockStatic(CommentMapper.class);
             MockedStatic<NewsMapper> newsMock = mockStatic(NewsMapper.class)) {

            NewsWithCommentsPagedDto result = newsService.getNewsWithCommentsPaged(100L, pageable);

            assertEquals(news.getId(), result.getId());
            assertEquals(news.getTitle(), result.getTitle());
            assertEquals(0, result.getComments().getTotalElements());
        }
    }

    @Test
    void getNewsWithCommentsPaged_shouldThrowIfNewsNotFound() {
        Pageable pageable = PageRequest.of(0, 5);
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> newsService.getNewsWithCommentsPaged(999L, pageable));
    }

    @Test
    void search_shouldReturnMatchingNews() {
        when(newsRepository.searchByTitleOrText("query")).thenReturn(List.of(news));
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            List<NewsDto> result = newsService.search("query");

            assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
        }
    }

    @Test
    void getNewsById_shouldReturnDto() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            NewsDto result = newsService.getNewsById(100L);

            assertEquals(dto, result);
        }
    }

    @Test
    void getNewsById_shouldThrowIfNotFound() {
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> newsService.getNewsById(999L));
    }

    @Test
    void createNews_shouldSaveAndReturnDto() {
        when(userRepository.findById(dto.getInsertedById())).thenReturn(Optional.of(user));
        when(newsRepository.save(any(News.class))).thenReturn(news);
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            NewsDto result = newsService.createNews(dto);

            assertEquals(dto, result);
            verify(newsRepository).save(any(News.class));
        }
    }

    @Test
    void createNews_shouldThrowIfUserNotFound() {
        when(userRepository.findById(dto.getInsertedById())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> newsService.createNews(dto));
    }

    @Test
    void updateNews_shouldModifyAndReturnDto() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));
        when(userRepository.findById(dto.getUpdatedById())).thenReturn(Optional.of(user));
        when(newsRepository.save(any(News.class))).thenReturn(news);
        try (MockedStatic<NewsMapper> mocked = mockStatic(NewsMapper.class)) {
            mocked.when(() -> NewsMapper.toDto(news)).thenReturn(dto);

            NewsDto result = newsService.updateNews(100L, dto);

            assertEquals(dto.getTitle(), result.getTitle());
            verify(newsRepository).save(news);
        }
    }

    @Test
    void updateNews_shouldThrowIfNewsNotFound() {
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> newsService.updateNews(999L, dto));
    }

    @Test
    void updateNews_shouldThrowIfUpdatedUserNotFound() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));
        when(userRepository.findById(dto.getUpdatedById())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> newsService.updateNews(100L, dto));
    }

    @Test
    void deleteNews_shouldRemoveIfExists() {
        when(newsRepository.existsById(100L)).thenReturn(true);

        newsService.deleteNews(100L);

        verify(newsRepository).deleteById(100L);
    }

    @Test
    void deleteNews_shouldThrowIfNotFound() {
        when(newsRepository.existsById(999L)).thenReturn(false);

        assertThrows(NewsNotFoundException.class, () -> newsService.deleteNews(999L));
    }
}
