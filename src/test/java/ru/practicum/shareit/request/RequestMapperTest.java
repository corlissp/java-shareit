package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class RequestMapperTest {

    public static final Integer ID = 1;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();
    private RequestMapper requestMapper;
    private Request request;
    private PostRequestDto postRequestDto;
    private ItemRepository itemRepository;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        request = new Request(ID, "description", ID, CREATED_DATE);
        postRequestDto = new PostRequestDto("description");
        requestMapper = new RequestMapper(new ItemMapper(new CommentMapper()));
    }

    @Test
    public void toModelTest() {
        Request result = requestMapper.toModel(postRequestDto, ID);

        assertNotNull(result);
        assertEquals(ID, result.getRequestor());
        assertEquals(postRequestDto.getDescription(), result.getDescription());
    }

    @Test
    public void toPostResponseDtoTest() {
        PostResponseRequestDto result = requestMapper.toPostResponseDto(request);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getCreated(), result.getCreated());
    }

    @Test
    public void toRequestWithItemsDtoTest() {
        RequestWithItemsDto result = requestMapper.toRequestWithItemsDto(request, new ArrayList<>());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }
}