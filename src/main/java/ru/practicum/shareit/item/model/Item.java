package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
@Entity(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false, length = 512)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @Column(name = "owner", nullable = false)
    private Integer owner;
    @Column(name = "request_id")
    private Integer requestId;
}
