package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Min Danil 04.11.2023
 */
@Data
@NoArgsConstructor
public class ItemInRequestDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private Integer owner;
}
