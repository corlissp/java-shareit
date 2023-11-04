package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

/**
 * @author Min Danil 27.09.2023
 */
@Service
public class UserService {
    private static final String NOT_FOUND_USER = "Не найден пользователь с id = ";
    private static final String EMAIL_CONFLICT = "Данный email уже занят пользователем";
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UserDto saveUser(UserDto userDTO) {
        return mapper.toUserDTO(repository.save(mapper.toUser(userDTO)));
    }

    public List<UserDto> getAllUsers() {
        return mapper.mapUserListToUserDtoList(repository.findAll());
    }

    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = update(id, userDto);
        return mapper.toUserDTO(repository.save(user));
    }

    public UserDto getUser(Integer id) {
        return mapper.toUserDTO(repository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + id)));
    }

    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }

    private User update(Integer id, UserDto patch) {
        patch.setId(id);
        if (!checkFreeEmail(mapper.toUser(patch)))
            throw new EmailConflictException(EMAIL_CONFLICT);
        UserDto old = getUser(id);
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            old.setName(name);
        }
        String oldEmail = old.getEmail();
        String newEmail = patch.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !oldEmail.equals(newEmail)) {
            old.setEmail(newEmail);
        }
        return mapper.toUser(old);
    }

    private boolean checkFreeEmail(User user) {
        List<User> list = repository.findAll();
        if (user.getEmail() == null)
            return true;
        for (User check: list) {
            if (user.getEmail().equals(check.getEmail()) && !Objects.equals(check.getId(), user.getId()))
                return false;
        }
        return true;
    }
}
