package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int itemId;
    private int bookerId;
    private BookingStatus status;
}
