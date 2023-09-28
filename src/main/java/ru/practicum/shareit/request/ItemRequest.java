package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {
    private int id;
    private String description;
    private int requester;
    private LocalDateTime createdTime;
}
