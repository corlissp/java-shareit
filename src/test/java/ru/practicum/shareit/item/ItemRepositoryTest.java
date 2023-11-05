package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User itemOwner;
    private Item item;
    private Request request;

    @BeforeEach
    public void beforeEach() {
        itemOwner = userRepository.save(new User(1, "owner", "owner@email"));
        User user = userRepository.save(new User(2, "user", "user@email"));
        item = itemRepository.save(new Item(
                1,
                "item",
                "description",
                true,
                itemOwner.getId(),
                null));

        request = requestRepository.save(new Request(
                null,
                "description",
                user.getId(),
                LocalDateTime.now()
        ));
    }

    @Test
    public void searchTest() {
        List<Item> result = itemRepository.search("description");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }

    @Test
    public void findAllTest() {
        Page<Item> result = itemRepository.findAll(itemOwner.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getOwner(), result.getContent().get(0).getOwner());
        assertEquals(item.getName(), result.getContent().get(0).getName());
        assertEquals(item.getDescription(), result.getContent().get(0).getDescription());
    }

    @Test
    public void findAllByRequestIdTest() {
        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }
}