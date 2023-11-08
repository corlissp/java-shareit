package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.validation.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Min Danil 27.09.2023
 */
@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotNull(groups = {Create.class})
    @Size(max = 512)
    private String name;
    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Size(max = 512)
    private String email;
}
