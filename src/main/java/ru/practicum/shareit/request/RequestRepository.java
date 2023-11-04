package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Min Danil 19.10.2023
 */
public interface RequestRepository extends JpaRepository<Request, Integer> {
}
