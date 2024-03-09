package ru.practicum.shareit.item.exception;

/**
 * @author Min Danil 28.09.2023
 */
public class NotFoundItemException extends RuntimeException {
    public NotFoundItemException(String message) {
        super(message);
    }
}
