package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemRequestService itemRequestService;

    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    public void beforeEach() {
        userDto = generateUserDto("user@email.com", "user");
        itemDto = generateItemDto();
        userDto = userService.saveUser(userDto);
        itemDto = itemService.saveItem(userDto.getId(), itemDto);
    }

    @AfterEach
    public void afterEach() {
        userService.deleteUser(userDto.getId());
        itemRepository.deleteById(itemDto.getId());
    }

    @Test
    public void saveTest() {
        TypedQuery<Item> query = em.createQuery("Select i from items i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertNotNull(item.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), (itemDto.getDescription()));
    }

    @Test
    public void findItemByIdTest() {
        UserDto savedOwnerDto = userService.saveUser(generateUserDto("user", "user@email.com"));
        ItemDto savedItemDto = itemService.saveItem(savedOwnerDto.getId(), itemDto);
        ItemDto searchedItemDto = itemService.getItemById(savedItemDto.getId(), savedOwnerDto.getId());

        assertNotNull(searchedItemDto);
        assertEquals(savedItemDto.getName(), searchedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), searchedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), searchedItemDto.getAvailable());
    }


    private ItemDto generateItemDto() {
        ItemDto itemDto = ItemDto.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        return itemDto;
    }

    private UserDto generateUserDto(String name, String email) {
        UserDto dto = UserDto.builder().build();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}