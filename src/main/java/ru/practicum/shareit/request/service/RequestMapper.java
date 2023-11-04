package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<RequestWithItemsDto> toRequestWithItemsDtoList(Page<Request> requests,
                                                                      ItemRepository repository) {
        return requests.stream()
                .map((Request request) -> {
                    List<Item> items = repository.findAllByRequestId(request.getId());
                    return toRequestWithItemsDto(request, items);
                }).collect(Collectors.toList());
    }

    public List<RequestWithItemsDto> toRequestWithItemsDtoList(List<Request> requests,
                                                                      ItemRepository repository) {
        List<RequestWithItemsDto> result = new ArrayList<>();
        if (requests != null && !requests.isEmpty()) {
            for (Request request : requests) {
                List<Item> items = repository.findAllByRequestId(request.getId());
                RequestWithItemsDto requestDto = toRequestWithItemsDto(request, items);
                result.add(requestDto);
            }
        }
        return result;
    }
}
