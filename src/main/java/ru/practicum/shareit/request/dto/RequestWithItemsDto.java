package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemInRequestDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Min Danil 04.11.2023
 */
@Data
@NoArgsConstructor
public class RequestWithItemsDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemInRequestDto> items;
}
