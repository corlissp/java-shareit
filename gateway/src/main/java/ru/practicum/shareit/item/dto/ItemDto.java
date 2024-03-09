package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.request.model.Request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private int id;
    @NotBlank(groups = {Create.class})
    @Size(max = 512)
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(max = 512)
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Integer owner;
    private Request request;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
