package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private ItemRequest request;
}
