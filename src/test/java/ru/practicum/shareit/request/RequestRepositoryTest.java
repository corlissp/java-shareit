package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private RequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private User requestor;
    private Request request;

    @BeforeEach
    public void beforeEach() {
        LocalDateTime dateTime = LocalDateTime.now();
        user = userRepository.save(new User(null, "user", "user@email.com"));
        requestor = userRepository.save(new User(null, "requestor", "requestor@email.com"));
        request = itemRequestRepository.save(new Request(null, "request", requestor.getId(), dateTime));
    }

    @Test
    public void findRequestByRequestorOrderByCreatedDescTest() {
        List<Request> result = itemRequestRepository
                .findRequestByRequestorOrderByCreatedDesc(requestor.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(request.getDescription(), result.get(0).getDescription());
        assertEquals(request.getRequestor(), result.get(0).getRequestor());
        assertEquals(request.getCreated(), result.get(0).getCreated());
    }

    @Test
    public void findAllTest() {
        Page<Request> result = itemRequestRepository.findAll(user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(request.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(request.getRequestor(), result.getContent().get(0).getRequestor());
        assertEquals(request.getCreated(), result.getContent().get(0).getCreated());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}