package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    public static final Integer ID = 1;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private Item item;
    private User user;
    private ItemDto itemDto;
    private Comment comment;
    private Booking booking;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemService(
                itemRepository,
                commentRepository,
                new CommentMapper(),
                userRepository,
                bookingRepository,
                new ItemMapper(new CommentMapper()),
                new UserService(userRepository, new UserMapper()));

        item = new Item(
                ID,
                "name",
                "description",
                true,
                ID,
                ID + 1);
        itemDto = ItemDto.builder()
                .id(ID)
                .name("name")
                .description("description")
                .available(true)
                .requestId(ID + 1)
                .build();

        user = new User(ID, "name", "user@emali.com");
        comment = new Comment(ID, "comment", item, user, CREATED_DATE);
        createCommentDto = new CreateCommentDto("comment");
        booking = new Booking(ID,
                CREATED_DATE,
                CREATED_DATE.plusDays(7),
                item,
                user,
                BookingStatus.APPROVED);
    }

    @Test
    public void createItemOwnerNotFoundTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> {
                    itemService.saveItem(ID, itemDto);
                });
        assertNotNull(e);
    }

    @Test
    public void createCommentTest() {
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingsForAddComments(any(Integer.class), any(Integer.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.saveComment(createCommentDto, ID, ID);

        assertNotNull(result);
        assertEquals(createCommentDto.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    public void createCommentExceptionTest() {
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingsForAddComments(any(Integer.class), any(Integer.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        CommentException result = assertThrows(CommentException.class, () -> {
            itemService.saveComment(createCommentDto, ID, ID);
        });

        assertNotNull(result);
    }

    @Test
    public void updateItemTest() {
        itemDto.setName("updatedName");
        item.setName("updatedName");

        when(commentRepository.findByItemId(any(Integer.class)))
                .thenReturn(new ArrayList<>());

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        ItemDto result = itemService.updateItem(itemDto.getId(), user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    public void findItemByIdTest() {
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findByItemId(any(Integer.class)))
                .thenReturn(new ArrayList<>());

        ItemDto result = itemService.getItemById(ID, ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void searchAvailableItemsByTextShouldReturnEmptyListForBlankText() {
        List<ItemDto> result = itemService.searchAvailableItemsByText("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(any(String.class));
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(ID, ID, itemDto);
        });

        assertNotNull(exception);
        verify(commentRepository, never()).findByItemId(any(Integer.class));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void saveItemShouldThrowNotFoundExceptionWhenOwnerNotFound() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> {
                    itemService.saveItem(ID, itemDto);
                });

        assertNotNull(exception);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void getAllItemsForOwnerTest() {
        List<Item> userItems = List.of(item);

        when(itemRepository.findAll())
                .thenReturn(userItems);

        List<ItemDto> result = itemService.getAllItems(ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemDto.getId(), result.get(0).getId());
        verify(commentRepository, times(1)).findByItemId(any(Integer.class));
    }

    @Test
    public void getAllItemsEmptyListTest() {
        when(itemRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getAllItems(ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, never()).findByItemId(any(Integer.class));
    }

    @Test
    public void deleteItemTest() {
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        itemService.deleteItem(ID, ID);

        verify(itemRepository, times(1)).deleteById(any(Integer.class));
    }

}