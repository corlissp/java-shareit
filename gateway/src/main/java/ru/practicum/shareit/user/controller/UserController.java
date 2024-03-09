package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                                               @Min(MIN_ID_VALUE)
                                               @PathVariable Integer userId) {
        log.info("Searching userId={}", userId);
        return userClient.findUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Searching all users");
        return userClient.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@NotNull(message = NULL_USER_ID_MESSAGE)
                                             @Min(MIN_ID_VALUE)
                                             @PathVariable Integer userId,
                                             @Validated({Update.class})
                                             @RequestBody UserDto userDto) {
        log.info("Updating userId={}, user {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                               @Min(MIN_ID_VALUE)
                               @PathVariable Integer userId) {
        log.info("Deleting userId={}", userId);
        userClient.deleteUserById(userId);
    }
}