package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    public static final int MIN_VALUE = 1;
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String NULL_ITEM_ID_MESSAGE = "itemID is null";
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({Create.class})
                                             @RequestBody ItemDto itemDto,
                                             @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                                             @Min(MIN_VALUE)
                                             @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated({Update.class})
                                                @RequestBody CreateCommentDto commentDto,
                                                @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                                                @Min(MIN_VALUE)
                                                @PathVariable Integer itemId,
                                                @NotNull(message = (NULL_USER_ID_MESSAGE))
                                                @Min(MIN_VALUE)
                                                @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated({Update.class})
                                             @RequestBody ItemDto itemDto,
                                             @NotNull(message = NULL_ITEM_ID_MESSAGE)
                                             @Min(MIN_VALUE)
                                             @PathVariable Integer itemId,
                                             @NotNull(message = NULL_USER_ID_MESSAGE)
                                             @Min(MIN_VALUE)
                                             @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@NotNull(message = NULL_ITEM_ID_MESSAGE)
                                               @Min(MIN_VALUE)
                                               @PathVariable Integer itemId,
                                               @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItems(@NotNull(message = NULL_USER_ID_MESSAGE)
                                               @RequestHeader(USER_ID_HEADER) Integer userId,
                                               @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                               @Min(MIN_VALUE) int from,
                                               @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                               @Min(MIN_VALUE) int size) {
        return itemClient.findAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByRequest(@RequestParam String text,
                                                     @RequestHeader(USER_ID_HEADER) Integer userId,
                                                     @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                     @Min(MIN_VALUE) int from,
                                                     @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                     @Min(MIN_VALUE) int size) {
        return itemClient.findItemsByRequest(text, userId, from, size);
    }
}