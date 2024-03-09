package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Min Danil 19.10.2023
 */
@Data
@Builder
public class BookingDetailedDto {
    private Integer id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private Item item;
    private String name;
}
