package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * @author Min Danil 27.09.2023
 */
@Service
public class UserService {
    private UserRepository repository;
    private UserMapper mapper;

    @Autowired
    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UserDto saveUser(UserDto userDTO) {
        return mapper.toUserDTO(repository.saveUser(mapper.toUser(userDTO)));
    }

    public List<UserDto> getAllUsers() {
        return mapper.mapUserListToUserDtoList(repository.getAllUsers());
    }

    public UserDto updateUser(UserDto userDto, Integer id) {
        return mapper.toUserDTO(repository.updateUser(id, mapper.toUser(userDto)));
    }

    public UserDto getUser(Integer id) {
        return mapper.toUserDTO(repository.getUserById(id));
    }

    public void deleteUser(Integer id) {
        repository.deleteUser(id);
    }
}
