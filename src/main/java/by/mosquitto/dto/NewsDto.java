package by.mosquitto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {
    private Long id;
    private String title;
    private String text;
    private LocalDateTime creationDate;
    private LocalDateTime lastEditDate;

    private Long insertedById;
    private Long updatedById;
}
