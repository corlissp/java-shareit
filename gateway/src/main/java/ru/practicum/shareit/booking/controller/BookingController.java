package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.validation.Create;


import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    public static final int MIN_VALUE = 0;
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Validated(Create.class) BookingPostDto dto,
                                                @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Creating booking {}, userId={}", dto, userId);
        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Path booking {}, approved={}. userId={}", bookingId, approved, userId);
        return bookingClient.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable Integer bookingId,
                                           @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookings(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                  @RequestHeader(USER_ID_HEADER) Integer userId,
                                                  @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                  @Min(MIN_VALUE) int from,
                                                  @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                  @PositiveOrZero int size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByItemOwner(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                     @RequestHeader(USER_ID_HEADER) Integer userId,
                                                     @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                     @Min(MIN_VALUE) int from,
                                                     @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                     @PositiveOrZero int size) {
        log.info("Get bookings owner with state{}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findAllByItemOwner(state, userId, from, size);
    }
}