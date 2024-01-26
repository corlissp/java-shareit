package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {

    public static final Integer ID = 1;
    public static final int FROM_VALUE = 0;
    public static final int SIZE_VALUE = 20;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestService requestService;
    private RequestRepository requestRepository;
    private RequestMapper requestMapper;

    private User user;
    private Request request;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        requestRepository = mock(RequestRepository.class);
        requestMapper = new RequestMapper(new ItemMapper(new CommentMapper()));
        requestService = new ItemRequestService(
                userRepository,
                itemRepository,
                requestRepository,
                requestMapper);

        request = new Request(ID, "description", ID, CREATED_DATE);
        user = new User(ID, "name", "user@emali.com");
    }

    @Test
    public void createRequestTest() {
        PostRequestDto inputDto = new PostRequestDto(request.getDescription());

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);

        PostResponseRequestDto responseDto = requestService.createRequest(inputDto, ID);

        assertNotNull(responseDto);
        assertEquals(ID, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void findAllByUserIdTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository
                .findRequestByRequestorOrderByCreatedDesc(any(Integer.class)))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findAllByRequestId(any(Integer.class)))
                .thenReturn(new ArrayList<>());

        List<RequestWithItemsDto> result = requestService.findAllByUserId(ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findAll(any(Integer.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        when(itemRepository.findAllByRequestId(any(Integer.class)))
                .thenReturn(new ArrayList<>());

        List<RequestWithItemsDto> result = requestService.findAll(FROM_VALUE, SIZE_VALUE, ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findById(any(Integer.class)))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestId(any(Integer.class)))
                .thenReturn(new ArrayList<>());


        RequestWithItemsDto result = requestService.findById(ID, ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void findAllByUserIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestService.findAllByUserId(ID);
        });

        assertNotNull(exception);
    }

    @Test
    void findAllShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestService.findAll(FROM_VALUE, SIZE_VALUE, ID);
        });

        assertNotNull(exception);
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestService.findById(ID, ID);
        });

        assertNotNull(exception);
    }

    @Test
    void createRequestShouldThrowNotFoundExceptionWhenUserNotFound() {
        PostRequestDto inputDto = new PostRequestDto(request.getDescription());

        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestService.createRequest(inputDto, ID);
        });

        assertNotNull(exception);
    }

}