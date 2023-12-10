package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    private User user;
    private UserMapper userMapper;

    @BeforeEach
    private void beforeEach() {
        user = new User(1, "user1", "user1@email.com");
        userMapper = new UserMapper();
    }

    @Test
    public void toDtoTest() {
        UserDto dto = userMapper.toUserDTO(user);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void toModelTest() {
        UserDto dto = new UserDto(1, "user1", "user1@email.com");
        User fromDtoUser = userMapper.toUser(dto);

        assertEquals(dto.getId(), fromDtoUser.getId());
        assertEquals(dto.getName(), fromDtoUser.getName());
        assertEquals(dto.getEmail(), fromDtoUser.getEmail());
    }

    @Test
    public void mapUserListToUserDtoList() {
        List<User> users = Collections.singletonList(user);
        List<UserDto> dtos = userMapper.mapUserListToUserDtoList(users);

        assertNotNull(dtos);
        assertEquals(users.get(0).getId(), dtos.get(0).getId());
        assertEquals(users.get(0).getName(), dtos.get(0).getName());
        assertEquals(users.get(0).getEmail(), dtos.get(0).getEmail());
    }
}