package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Min Danil 19.10.2023
 */
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findRequestByRequestorOrderByCreatedDesc(Integer requestor);

    @Query("select r from requests r where r.requestor <> ?1")
    Page<Request> findAll(Integer userId, Pageable pageable);
}
