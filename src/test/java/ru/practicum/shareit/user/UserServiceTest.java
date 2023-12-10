package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    public static final Integer ID = 1;

    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private User user;

    @BeforeEach
    void beforeEach() {
        userMapper = new UserMapper();
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository, userMapper);
        user = new User(1, "user1", "user1@email.com");
    }

    @Test
    void createUserTest() {
        User savedUser = new User();
        savedUser.setName(user.getName());
        savedUser.setEmail(user.getEmail());
        UserDto savedDto = userMapper.toUserDTO(savedUser);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = userService.saveUser(savedDto);

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals(savedUser.getName(), userDto.getName());
        assertEquals(savedUser.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        user.setName("updated name");
        UserDto inputDto = userMapper.toUserDTO(user);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.updateUser(inputDto, ID);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getUser(ID);

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(any(Integer.class));
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUser(ID);
        verify(userRepository, times(1)).deleteById(ID);
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        List<UserDto> dtos = userService.getAllUsers();

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(user.getId(), dtos.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUserShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        UserDto userDto = new UserDto();
        userDto.setId(1);

        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, 1));

        verify(userRepository, never()).save(any(User.class));
    }

}