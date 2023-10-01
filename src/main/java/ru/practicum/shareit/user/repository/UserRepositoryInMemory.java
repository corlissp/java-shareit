package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailConflictException;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

/**
 * @author Min Danil 27.09.2023
 */
@Repository
public class UserRepositoryInMemory implements UserRepository {
    private static final String NOT_FOUND_USER = "Не найден пользователь с id = ";
    private static final String EMAIL_CONFLICT = "Данный email уже занят пользователем";
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int freeId = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User saveUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailConflictException(EMAIL_CONFLICT);
        }
        user.setId(getFreeId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundUserException(NOT_FOUND_USER + id);
        }
        return users.get(id);
    }

    @Override
    public User updateUser(Integer userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundUserException(NOT_FOUND_USER + userId);
        }

        User oldUser = users.get(userId);
        if (emails.contains(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
            throw new EmailConflictException(EMAIL_CONFLICT);
        }

        emails.remove(oldUser.getEmail());

        User newUser = User.builder()
                .id(userId)
                .name(Objects.requireNonNullElse(user.getName(), oldUser.getName()))
                .email(Objects.requireNonNullElse(user.getEmail(), oldUser.getEmail()))
                .build();

        users.put(userId, newUser);
        emails.add(newUser.getEmail());
        return users.get(userId);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundUserException(NOT_FOUND_USER + id);
        }
        User user = users.get(id);
        emails.remove(user.getEmail());
        users.remove(id);
    }

    private int getFreeId() {
        return freeId++;
    }
}
