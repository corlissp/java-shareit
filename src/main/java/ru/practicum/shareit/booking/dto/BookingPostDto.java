package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * @author Min Danil 12.10.2023
 */
@Data
@Builder
public class BookingPostDto {
    private Integer id;
    private Integer itemId;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;
}
