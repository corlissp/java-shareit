package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService service;

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
    public UserDto createUser(@Validated @RequestBody UserDto user) {
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
