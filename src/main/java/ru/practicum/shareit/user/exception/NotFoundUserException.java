package ru.practicum.shareit.user.exception;

/**
 * @author Min Danil 27.09.2023
 */
public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String message) {
        super(message);
    }
}
