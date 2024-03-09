package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exception.InvalidBookingException;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.exception.NotFoundItemException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void handleItemNotFoundExceptionTest() {
        NotFoundItemException e = new NotFoundItemException("Вещь не найдена ");
        ErrorHandler.ErrorResponse errorResponse = handler.handleNotFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleNotFoundExceptionTest() {
        NotFoundException e = new NotFoundException("Ошибка 404 ");
        ErrorHandler.ErrorResponse errorResponse = handler.handleNotFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleEmailConflictExceptionTest() {
        EmailConflictException e = new EmailConflictException("Недопустимый email ");
        ErrorHandler.ErrorResponse errorResponse = handler.handleEmailConflictException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Недопустимый email ");
    }

    @Test
    public void handleBookingExceptionTest() {
        InvalidBookingException e = new InvalidBookingException("Ошибка бронирования 400: ");
        ErrorHandler.ErrorResponse errorResponse = handler.handleBookingException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Ошибка бронирования 400: ");
    }

    @Test
    public void handleCommentExceptionTest() {
        CommentException e = new CommentException("Ошибка в комментарии ");
        ErrorHandler.ErrorResponse errorResponse = handler.handleCommentException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Ошибка в комментарии ");
    }

}