package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User booker;
    private User itemOwner;
    private Booking booking;
    private LocalDateTime end;
    private LocalDateTime start;
    private BookingStatus bookingStatus;

    @BeforeEach
    public void beforeEach() {
        start = LocalDateTime.now().plusDays(2);
        end = start.plusDays(7);
        bookingStatus = BookingStatus.APPROVED;

        itemOwner = userRepository.save(new User(null, "user 1", "user1@email.com"));
        booker = userRepository.save(new User(null, "user 2", "user2@email.com"));
        item = itemRepository.save(Item.builder()
                        .name("item 1")
                        .description("description")
                        .owner(itemOwner.getId())
                        .available(true)
                        .build());
        booking = bookingRepository.save(Booking.builder()
                        .start(start)
                        .end(end)
                        .item(item)
                        .booker(booker)
                        .status(bookingStatus)
                        .build());
    }

    @Test
    public void findByBookerIdAndEndIsBeforeTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndEndIsBefore(booker.getId(), LocalDateTime.now().plusDays(10), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdAndStartIsAfterTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfter(booker.getId(), LocalDateTime.now().plusDays(1), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdAndStatusTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdAndStatus(booker.getId(), bookingStatus, Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findByBookerIdTest() {
        Page<Booking> result = bookingRepository.findByBookerId(booker.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndStatusTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerAndStatus(itemOwner.getId(), bookingStatus, Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndEndIsBeforeTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerAndEndIsBefore(
                        itemOwner.getId(), LocalDateTime.now().plusDays(30), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerAndStartIsAfterTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwnerAndStartIsAfter(itemOwner.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemOwnerTest() {
        Page<Booking> result = bookingRepository
                .findBookingByItemOwner(itemOwner.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    public void findBookingByItemIdAndEndBeforeTest() {
        List<Booking> result = bookingRepository
                .findBookingByItemIdAndStartBefore(item.getId(), LocalDateTime.now().plusDays(30), Sort.unsorted());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void findBookingByItemIdAndStartAfterTest() {
        List<Booking> result = bookingRepository
                .findBookingByItemIdAndStartAfter(item.getId(), LocalDateTime.now(), Sort.unsorted());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void findByBookerIdCurrentTest() {
        Page<Booking> result = bookingRepository
                .findByBookerIdCurrent(booker.getId(), start.plusDays(1), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void findBookingsByItemOwnerCurrentTest() {
        Page<Booking> result = bookingRepository
                .findBookingsByItemOwnerCurrent(itemOwner.getId(), end.minusDays(5), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void findBookingsForAddCommentsTest() {
        List<Booking> result = bookingRepository
                .findBookingsForAddComments(item.getId(), booker.getId(), end.plusDays(1));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}