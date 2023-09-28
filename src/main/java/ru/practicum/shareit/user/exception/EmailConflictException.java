package ru.practicum.shareit.user.exception;

/**
 * @author Min Danil 27.09.2023
 */
public class EmailConflictException extends RuntimeException {
    public EmailConflictException(String message) {
        super(message);
    }
}
