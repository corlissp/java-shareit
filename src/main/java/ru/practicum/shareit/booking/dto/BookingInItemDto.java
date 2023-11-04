package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Min Danil 12.10.2023
 */
@Data
@Builder
public class BookingInItemDto {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
