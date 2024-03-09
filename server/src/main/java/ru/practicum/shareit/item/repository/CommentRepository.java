package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

/**
 * @author Min Danil 12.10.2023
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemId(Integer itemId);
}
