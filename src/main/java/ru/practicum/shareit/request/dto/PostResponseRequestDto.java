package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author Min Danil 04.11.2023
 */
@Data
@NoArgsConstructor
public class PostResponseRequestDto {
    private Integer id;
    @Size(max = 512)
    private String description;
    private LocalDateTime created;
}
