package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @author Min Danil 04.11.2023
 */
@Data
@NoArgsConstructor
public class ItemInRequestDto {
    private Integer id;
    @Size(max = 512)
    private String name;
    @Size(max = 512)
    private String description;
    private Boolean available;
    private Integer requestId;
    private Integer owner;
}
