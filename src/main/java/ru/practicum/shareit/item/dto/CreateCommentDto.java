package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.validation.Create;

import javax.validation.constraints.NotBlank;

/**
 * @author Min Danil 12.10.2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    @NotBlank(groups = Create.class)
    private String text;
}
