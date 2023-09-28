package ru.practicum.shareit.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.user.exception.EmailConflictException;
import ru.practicum.shareit.user.exception.NotFoundUserException;

/**
 * @author Min Danil 28.09.2023
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundItemException e) {
        String message = e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundUserException e) {
        String message = e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotFoundException(final EmailConflictException e) {
        String message = e.getMessage();

        log.error(message);
        return new ErrorResponse(message);
    }

    @RequiredArgsConstructor
    @Getter
    private static class ErrorResponse {
        private final String error;
    }
}

