package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Min Danil 19.10.2023
 */
@Component
public class BookingMapper {
    public static Booking toModel(BookingPostDto dto, Item item, User user) {
        return Booking.builder()
                .booker(user)
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingPostResponseDto toPostResponseDto(Booking booking, Item item) {
        return BookingPostResponseDto.builder()
                .id(booking.getId())
                .item(item)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }

    public static BookingResponseDto toResponseDto(Booking booking, User booker, Item item) {
        return BookingResponseDto.builder()
                .name(item.getName())
                .item(item)
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booker)
                .status(booking.getStatus())
                .id(booking.getId())
                .build();
    }

    public static BookingDetailedDto toDetailedDto(Booking booking) {
        return BookingDetailedDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .name(booking.getItem().getName())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();
    }

    public static BookingInItemDto bookingInItemDto(Booking booking) {
        if (booking == null) return null;
        return BookingInItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static List<BookingDetailedDto> toListDetailedDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDetailedDto).collect(Collectors.toList());
    }
}
