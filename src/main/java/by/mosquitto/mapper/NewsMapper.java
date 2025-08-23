package by.mosquitto.mapper;

import by.mosquitto.dto.NewsDto;
import by.mosquitto.entity.News;

public class NewsMapper {

    public static NewsDto toDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .text(news.getText())
                .creationDate(news.getCreationDate())
                .lastEditDate(news.getLastEditDate())
                .insertedById(news.getCreatedByUser().getId())
                .updatedById(news.getUpdatedByUser() != null ? news.getUpdatedByUser().getId() : null)
                .build();
    }
}