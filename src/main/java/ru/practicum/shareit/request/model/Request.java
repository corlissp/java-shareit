package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "requestor", nullable = false)
    private Integer requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
