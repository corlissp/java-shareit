package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * @author Min Danil 28.09.2023
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(" select i from items i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text);

    @Query("select i from items i where i.owner = ?1")
    Page<Item> findAll(Integer userId, Pageable pageable);

    List<Item> findAllByRequestId(Integer requestId);

    List<Item> findAllByRequestIdIn(List<Integer> requestIds);
}
