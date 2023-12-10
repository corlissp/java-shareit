package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.validation.Create;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен get-запрос /users");
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        log.info("Получен get-запрос /users/" + id);
        return service.getUser(id);
    }

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto user) {
        log.info("Получен post-запрос /users");
        return service.saveUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable Integer id) {
        log.info("Получен patch-запрос /users/" + id);
        return service.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Получен delete-запрос /users/" + id);
        service.deleteUser(id);
    }

}
