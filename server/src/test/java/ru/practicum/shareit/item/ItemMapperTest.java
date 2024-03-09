package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    public static final Integer ID = 1;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private ItemMapper itemMapper;

    @BeforeEach
    public void beforeEach() {
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
                .owner(ID)
                .build();

        User user = new User(ID, "name", "user@emali.com");
        comment = new Comment(ID, "comment", item, user, CREATED_DATE);

        Booking booking = new Booking(ID,
                CREATED_DATE,
                CREATED_DATE.plusDays(7),
                item,
                user,
                BookingStatus.APPROVED);
        itemMapper = new ItemMapper(new CommentMapper());
    }

    @Test
    public void toDto() {
        ItemDto resultWithoutBookings = itemMapper
                .toDto(item, Collections.singletonList(comment));
        ItemDto resultWithBookings = itemMapper
                .toDto(item, null, null, Collections.singletonList(comment));

        assertNotNull(resultWithoutBookings);
        assertNotNull(resultWithBookings);
        assertEquals(item.getId(), resultWithBookings.getId());
        assertEquals(item.getId(), resultWithoutBookings.getId());
        assertFalse(resultWithBookings.getComments().isEmpty());
        assertFalse(resultWithBookings.getComments().isEmpty());
    }

    @Test
    public void toModel() {
        Item result = itemMapper.toItem(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(ID, result.getOwner());
    }

    @Test
    public void toRequestItemDto() {
        ItemInRequestDto result = itemMapper.toRequestItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    public void toItem() {
        Item result = itemMapper.toItem(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(ID, result.getOwner());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    public void mapItemListToItemDtoList() {
        List<Item> itemList = Collections.singletonList(item);
        List<ItemDto> result = itemMapper.mapItemListToItemDtoList(itemList);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
        assertEquals(item.getRequestId(), result.get(0).getRequestId());
        assertEquals(item.getOwner(), result.get(0).getOwner());
    }

    @Test
    public void toDtoWithBookings() {
        Booking booking = new Booking(ID,
                CREATED_DATE,
                CREATED_DATE.plusDays(7),
                item,
                new User(ID, "booker", "booker@emali.com"),
                BookingStatus.APPROVED);

        ItemDto result = itemMapper.toDto(item, booking, booking, Collections.singletonList(comment));

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertEquals(item.getOwner(), result.getOwner());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    public void toDtoWithoutBookings() {
        ItemDto result = itemMapper.toDto(item, Collections.singletonList(comment));

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertEquals(item.getOwner(), result.getOwner());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    public void toRequestItemDtoList() {
        List<ItemInRequestDto> result = itemMapper
                .toRequestItemDtoList(Collections.singletonList(item));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
        assertEquals(item.getRequestId(), result.get(0).getRequestId());
        assertEquals(item.getOwner(), result.get(0).getOwner());

    }
}