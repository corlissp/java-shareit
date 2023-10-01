package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * @author Min Danil 27.09.2023
 */
public interface UserRepository {
    List<User> getAllUsers();

    User saveUser(User user);

    User getUserById(Integer id);

    User updateUser(Integer userId, User user);

    void deleteUser(Integer id);
}
