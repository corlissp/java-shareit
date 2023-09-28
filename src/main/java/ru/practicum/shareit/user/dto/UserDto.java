package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Min Danil 27.09.2023
 */
@Data
@Builder
public class UserDto {
    private Integer id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
