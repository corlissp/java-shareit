package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Min Danil 27.09.2023
 */
@Component
public class UserMapper {
    public User toUser(UserDto userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .build();
    }

    public UserDto toUserDTO(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> mapUserListToUserDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toUserDTO(user));
        }
        return result;
    }
}
