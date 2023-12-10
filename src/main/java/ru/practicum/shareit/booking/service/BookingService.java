package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.InvalidBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

/**
 * @author Min Danil 12.10.2023
 */
@Service
public class BookingService {
    public static final String DENIED_ACCESS_MESSAGE = "пользователь не является владельцем вещи или брони userId: ";
    public static final String BOOKING_INVALID_MESSAGE = "Недопустимые значения времени бронирования: ";
    public static final String INVALID_BUCKING = "Нельзя забронировать свою же вещь";
    public static final String UNAVAILABLE_BOOKING_MESSAGE = "В данный момент невозможно забронировать item: ";
    private static final String NOT_FOUND_USER = "Не найден пользователь с id = ";
    private static final String NOT_FOUND_ITEM = "Не найден item с id = ";
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    public static final Sort SORT = Sort.by("start").descending();

    @Autowired
    public BookingService(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }


    public BookingPostResponseDto createBooking(BookingPostDto dto, Integer userId) {
        if (!isStartBeforeEnd(dto)) {
            throw new InvalidBookingException(BOOKING_INVALID_MESSAGE +
                    "start: " + dto.getStart() + " end: " + dto.getEnd() + " now: ");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM + dto.getItemId()));
        if (userId.equals(item.getOwner())) {
            throw new NotFoundException(INVALID_BUCKING);
        }
        if (!item.getAvailable()) {
            throw new InvalidBookingException(UNAVAILABLE_BOOKING_MESSAGE + item.getId());
        }

        Booking booking = BookingMapper.toModel(dto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toPostResponseDto(booking, item);
    }

    public BookingResponseDto patchBooking(Integer bookingId, Boolean approved, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();

        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException(DENIED_ACCESS_MESSAGE + userId);
        }

        BookingStatus status = convertToStatus(approved);

        if (booking.getStatus().equals(status)) {
            throw new InvalidBookingException("");
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(booking, booking.getBooker(), item);
    }

    public BookingDetailedDto findById(Integer bookingId, Integer userId) {
        checkIfUserExists(userId);
        checkBookingExists(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Integer itemOwner = booking.getItem().getOwner();
        Integer bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            throw new NotFoundException(DENIED_ACCESS_MESSAGE + userId);
        }
        return BookingMapper.toDetailedDto(booking);
    }

    public List<BookingDetailedDto> findAllByBooker(String stateValue, Integer userId, int from, int size) {
        checkIfUserExists(userId);
        State status = parseState(stateValue);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size, SORT);

        switch (status) {
            case REJECTED :
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, REJECTED, pageable).toList();
                break;
            case WAITING :
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, WAITING, pageable).toList();
                break;
            case CURRENT :
                bookings = bookingRepository.findByBookerIdCurrent(userId, now, pageable).toList();
                break;
            case FUTURE :
                bookings = bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, now, pageable).toList();
                break;
            case PAST :
                bookings = bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, now, pageable).toList();
                break;
            case ALL :
                bookings = bookingRepository.findByBookerId(userId, pageable).toList();
                break;
        }
        return BookingMapper.toListDetailedDto(bookings);
    }

    public List<BookingDetailedDto> findAllByItemOwner(String stateValue, Integer userId, int from, int size) {
        checkIfUserExists(userId);
        State state = parseState(stateValue);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size, SORT);

        switch (state) {
            case REJECTED :
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, REJECTED, pageable).toList();
                break;
            case WAITING :
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, WAITING, pageable).toList();
                break;
            case CURRENT :
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now, pageable).toList();
                break;
            case FUTURE :
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStartIsAfter(userId, now, pageable).toList();
                break;
            case PAST :
                bookings = bookingRepository
                        .findBookingByItemOwnerAndEndIsBefore(userId, now, pageable).toList();
                break;
            case ALL :
                bookings = bookingRepository
                        .findBookingByItemOwner(userId, pageable).toList();
                break;
        }
        return BookingMapper.toListDetailedDto(bookings);
    }

    private void checkIfUserExists(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
    }

    private void checkBookingExists(Integer bookingId) {
        bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(""));
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return approved ? BookingStatus.APPROVED : REJECTED;
    }

    private State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidBookingException("Unknown state: " + state);
        }
        return status;
    }

    private boolean isStartBeforeEnd(BookingPostDto dto) {
        if (dto.getEnd() == null || dto.getStart() == null)
            return false;
        return dto.getStart().isBefore(dto.getEnd());
    }
}
