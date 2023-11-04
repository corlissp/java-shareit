package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.validation.Create;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestController {
    public static final int MIN_VALUE = 0;
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    public PostResponseRequestDto createRequest(@Validated({Create.class})
                                                @RequestBody PostRequestDto postRequestDto,
                                                @RequestHeader(USER_ID_HEADER) Integer userId) {
        return service.createRequest(postRequestDto, userId);
    }

    @GetMapping
    public List<RequestWithItemsDto> findAllByUserId(@RequestHeader(USER_ID_HEADER) Integer userId) {
        return service.findAllByUserId(userId);
    }

    @GetMapping ("/all")
    public List<RequestWithItemsDto> findAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                             @Min(MIN_VALUE) int from,
                                             @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                             @Min(MIN_VALUE) int size,
                                             @RequestHeader(USER_ID_HEADER) Integer userId) {
        return service.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto findById(@PathVariable Integer requestId,
                                        @RequestHeader(USER_ID_HEADER) Integer userId) {
        return service.findById(requestId, userId);
    }
}
