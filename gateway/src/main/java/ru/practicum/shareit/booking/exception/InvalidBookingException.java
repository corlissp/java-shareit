package ru.practicum.shareit.booking.exception;

/**
 * @author Min Danil 19.10.2023
 */
public class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String message) {
        super(message);
    }
}
