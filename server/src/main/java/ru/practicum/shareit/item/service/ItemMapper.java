package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Min Danil 28.09.2023
 */
@Component
public class ItemMapper {

    private final CommentMapper commentMapper;

    @Autowired
    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();
    }

    public ItemDto toDto(Item item, Booking lastBooking,
                         Booking nextBooking, List<Comment> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .lastBooking(BookingMapper.bookingInItemDto(lastBooking))
                .nextBooking(BookingMapper.bookingInItemDto(nextBooking))
                .comments(commentMapper.toCommentDtoList(comments))
                .requestId(item.getRequestId())
                .build();
    }

    public ItemDto toDto(Item item, List<Comment> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .comments(commentMapper.toCommentDtoList(comments))
                .requestId(item.getRequestId())
                .build();
    }

    public List<ItemDto> mapItemListToItemDtoList(List<Item> userItems) {
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> result = new ArrayList<>();
        for (Item item : userItems) {
            ItemDto itemDto = toDto(item);
            result.add(itemDto);
        }
        return result;
    }

    public ItemInRequestDto toRequestItemDto(Item item) {
        ItemInRequestDto dto = new ItemInRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        dto.setOwner(item.getOwner());
        return dto;
    }

    public List<ItemInRequestDto> toRequestItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toRequestItemDto)
                .collect(Collectors.toList());
    }

}
