package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Получен get-запрос /items");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId, @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Получен get-запрос /items/" + itemId);
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader(USER_ID_HEADER) Integer userId,
                            @Validated({Create.class}) @RequestBody ItemDto itemDTO) {
        log.info("Получен post-запрос /items");
        return itemService.saveItem(userId, itemDTO);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Validated({Update.class}) @RequestBody CreateCommentDto commentDto,
                                  @NotNull @PathVariable Integer itemId,
                                  @NotNull @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Получен post-запрос /" + itemId +  "/comment");
        return itemService.saveComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Integer userId,
            @Validated({Update.class}) @RequestBody ItemDto itemDTO,
            @PathVariable Integer itemId) {
        log.info("Получен patch-запрос /items/" + itemId);
        return itemService.updateItem(itemId, userId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) Integer userId,
                           @PathVariable Integer itemId) {
        log.info("Получен delete-запрос /items/" + itemId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Получен get-запрос /items/search?text=" + text);
        return itemService.searchAvailableItemsByText(text);
    }

}
