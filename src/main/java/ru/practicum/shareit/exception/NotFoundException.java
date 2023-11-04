package ru.practicum.shareit.exception;

/**
 * @author Min Danil 27.09.2023
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
