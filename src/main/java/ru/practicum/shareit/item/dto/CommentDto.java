package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Min Danil 12.10.2023
 */
@Data
@Builder
public class CommentDto {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
