package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Min Danil 04.11.2023
 */
@Component
public class RequestMapper {

    private final ItemMapper itemMapper;

    @Autowired
    public RequestMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public Request toModel(PostRequestDto dto, Integer requestor) {
        Request request = new Request();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public PostResponseRequestDto toPostResponseDto(Request request) {
        PostResponseRequestDto dto = new PostResponseRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public RequestWithItemsDto toRequestWithItemsDto(Request request, List<Item> items) {
        List<ItemInRequestDto> itemDtos = itemMapper.toRequestItemDtoList(items);
        RequestWithItemsDto dto = new RequestWithItemsDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(itemDtos);
        return dto;
    }
}
