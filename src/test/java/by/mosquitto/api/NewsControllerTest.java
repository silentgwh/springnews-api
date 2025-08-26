package by.mosquitto.api;

import by.mosquitto.dto.CommentDto;
import by.mosquitto.dto.NewsDto;
import by.mosquitto.dto.NewsWithCommentsPagedDto;
import by.mosquitto.service.contract.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    private NewsDto news;
    private CommentDto comment;

    @BeforeEach
    void setUp() {
        news = NewsDto.builder()
                .id(1L)
                .title("Test Title")
                .text("Test Text")
                .creationDate(LocalDateTime.now().minusDays(1))
                .lastEditDate(LocalDateTime.now())
                .insertedById(100L)
                .updatedById(101L)
                .build();

        comment = CommentDto.builder()
                .id(10L)
                .text("Nice article")
                .creationDate(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    void getAllNews_shouldReturnListOfNews() {
        List<NewsDto> newsList = List.of(news);
        Mockito.when(newsService.getAllNews()).thenReturn(newsList);

        ResponseEntity<List<NewsDto>> response = newsController.getAllNews();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(newsList, response.getBody());
    }

    @Test
    void getNewsPaged_shouldReturnPagedNews() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        Page<NewsDto> page = new PageImpl<>(List.of(news), pageable, 1);
        Mockito.when(newsService.getNewsPaged(pageable)).thenReturn(page);

        ResponseEntity<Page<NewsDto>> response = newsController.getNewsPaged(0, 10, "creationDate", "desc");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(page, response.getBody());
    }

    @Test
    void getNewsWithCommentsPaged_shouldReturnNewsWithComments() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("creationDate").descending());
        Page<CommentDto> commentsPage = new PageImpl<>(List.of(comment), pageable, 1);

        NewsWithCommentsPagedDto dto = NewsWithCommentsPagedDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .text(news.getText())
                .creationDate(news.getCreationDate())
                .lastEditDate(news.getLastEditDate())
                .insertedById(news.getInsertedById())
                .updatedById(news.getUpdatedById())
                .comments(commentsPage)
                .build();

        Mockito.when(newsService.getNewsWithCommentsPaged(1L, pageable)).thenReturn(dto);

        ResponseEntity<NewsWithCommentsPagedDto> response = newsController.getNewsWithCommentsPaged(1L, 0, 5, "creationDate", "desc");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getComments().getTotalElements());
    }

    @Test
    void search_shouldReturnMatchingNews() {
        List<NewsDto> results = List.of(news);
        Mockito.when(newsService.search("Test")).thenReturn(results);

        ResponseEntity<List<NewsDto>> response = newsController.search("Test");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(results, response.getBody());
    }

    @Test
    void get_shouldReturnNewsById() {
        Mockito.when(newsService.getNewsById(1L)).thenReturn(news);

        ResponseEntity<NewsDto> response = newsController.get(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(news, response.getBody());
    }

    @Test
    void create_shouldReturnCreatedNews() {
        Mockito.when(newsService.createNews(news)).thenReturn(news);

        ResponseEntity<NewsDto> response = newsController.create(news);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(news, response.getBody());
    }

    @Test
    void update_shouldReturnUpdatedNews() {
        Mockito.when(newsService.updateNews(1L, news)).thenReturn(news);

        ResponseEntity<NewsDto> response = newsController.update(1L, news);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(news, response.getBody());
    }

    @Test
    void delete_shouldReturnNoContent() {
        Mockito.doNothing().when(newsService).deleteNews(1L);

        ResponseEntity<Void> response = newsController.delete(1L);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
