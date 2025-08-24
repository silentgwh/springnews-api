package by.mosquitto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsWithCommentsPagedDto {
    private Long id;
    private String title;
    private String text;
    private LocalDateTime creationDate;
    private LocalDateTime lastEditDate;
    private Long insertedById;
    private Long updatedById;
    private Page<CommentDto> comments;
}
