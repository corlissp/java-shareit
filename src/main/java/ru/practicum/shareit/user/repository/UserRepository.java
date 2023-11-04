package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * @author Min Danil 27.09.2023
 */

public interface UserRepository extends JpaRepository<User, Integer> {
}
