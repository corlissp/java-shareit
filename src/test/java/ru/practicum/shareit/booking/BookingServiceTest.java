package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.InvalidBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    public static final Integer ID = 1;
    public static final int FROM_VALUE = 0;
    public static final int SIZE_VALUE = 20;
    public static final LocalDateTime DATE = LocalDateTime.now();

    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingPostDto bookingPostDto;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingService(userRepository, itemRepository, bookingRepository);

        bookingPostDto = BookingPostDto.builder()
                .id(ID)
                .itemId(ID)
                .start(DATE)
                .end(DATE.plusDays(7))
                .build();
        user = new User(ID, "name", "user@emali.com");
        owner = new User(ID + 1, "owner", "user2@email.ru");
        item = new Item(
                ID,
                "name",
                "description",
                true,
                ID + 1,
                ID + 1);
        booking = new Booking(ID,
                DATE,
                DATE.plusDays(7),
                item,
                user,
                BookingStatus.APPROVED);
    }

    @Test
    public void createBookingTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingPostResponseDto result = bookingService.createBooking(bookingPostDto, ID);

        assertNotNull(result);
        assertEquals(bookingPostDto.getItemId(), result.getItem().getId());
        assertEquals(bookingPostDto.getStart(), result.getStart());
        assertEquals(bookingPostDto.getEnd(), result.getEnd());
    }


    @Test
    public void patchBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingResponseDto result = bookingService.patchBooking(ID, true, ID + 1);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void patchBookingNoSuchElementExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(ID);
        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.patchBooking(ID, true, ID + 1);
                });
        assertNotNull(e);
    }


    @Test
    public void findByIdTest() {
        item.setOwner(owner.getId());

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(booking));

        BookingDetailedDto result = bookingService.findById(ID, ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
    }

    @Test
    public void findByIdNoSuchElementExceptionTest() {
        user.setId(ID + 10);
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findById(ID, ID);
                });
        assertNotNull(e);
    }

    @Test
    public void findAllByBookerStateRejectedTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerIdAndStatus(any(Integer.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("REJECTED", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateWaitingTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerIdAndStatus(any(Integer.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("WAITING", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateCurrentTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerIdCurrent(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("CURRENT", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateFutureTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerIdAndStartIsAfter(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("FUTURE", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStatePastTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerIdAndEndIsBefore(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("PAST", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateAllTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findByBookerId(any(Integer.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByBooker("ALL", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateRejectedTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingByItemOwnerAndStatus(any(Integer.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("REJECTED", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateWaitingTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingByItemOwnerAndStatus(any(Integer.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("WAITING", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateCurrentTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingsByItemOwnerCurrent(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("CURRENT", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateFutureTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingByItemOwnerAndStartIsAfter(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("FUTURE", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStatePastTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingByItemOwnerAndEndIsBefore(any(Integer.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("PAST", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateAllTest() {

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findBookingByItemOwner(any(Integer.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService
                .findAllByItemOwner("ALL", ID, FROM_VALUE, SIZE_VALUE);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void createBookingInvalidTimeTest() {
        bookingPostDto.setStart(DATE.plusDays(7));
        bookingPostDto.setEnd(DATE);

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(item));

        InvalidBookingException e = assertThrows(InvalidBookingException.class,
                () -> {
                    bookingService.createBooking(bookingPostDto, ID);
                });

        assertEquals(BookingService.BOOKING_INVALID_MESSAGE + "start: " + DATE.plusDays(7) + " end: " + DATE + " now: ", e.getMessage());
    }

    @Test
    public void findByIdAccessDeniedTest() {
        user.setId(ID + 10);

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(booking));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findById(ID, ID);
                });

        assertEquals(BookingService.DENIED_ACCESS_MESSAGE + ID, e.getMessage());
    }

    @Test
    public void patchBookingAlreadyRejectedTest() {
        when(bookingRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> {
                    bookingService.patchBooking(ID, true, ID + 1);
                });

        assertNotNull(e);
    }





}