package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONTROLLER_URL = "/users";

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void findAllUsersTest() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void getAllUsers_whenInvoked_thenReturnedStatusOk() {
        mvc
                .perform(
                        get(CONTROLLER_URL)
                )
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    void getUserById_whenUserIdIsValid_thenReturnedUserAndStatusOk() {
        int userId = 1;
        UserMapper userMapper = new UserMapper();
        User user = User.builder().id(userId).build();
        UserDto userDTO = userMapper.toUserDTO(user);

        when(userService.getUser(userId)).thenReturn(userDTO);

        String json = mvc
                .perform(
                        get(CONTROLLER_URL + "/{userId}", userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDTO), json);
        assertEquals(userId, userDTO.getId());
    }

    @Test
    @SneakyThrows
    void getUserById_whenUserIdIsNotValid_thenReturnedBadRequest() {
        String userId = "userId";

        mvc.perform(
                        get(CONTROLLER_URL + "/{userId}", userId)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUser(anyInt());
    }

    @Test
    @SneakyThrows
    void saveUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto userDTO = UserDto.builder().build();

        mvc.perform(
                        post(CONTROLLER_URL)
                                .content(mapper.writeValueAsString(userDTO))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserDto.class));
    }

    @Test
    public void createUserTest() throws Exception {
        Integer userId = 1;
        UserDto userDto = createTestUserDto(userId);

        when(userService.saveUser(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).saveUser(any(UserDto.class));
    }

    @Test
    public void findUserByIdTest() throws Exception {
        Integer userId = 1;
        UserDto userDto = createTestUserDto(userId);

        when(userService.getUser(userId))
                .thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    public void updateUserTest() throws Exception {
        Integer userId = 1;
        UserDto userDto = createTestUserDto(userId);
        userDto.setName("updatedName");

        when(userService.updateUser(any(UserDto.class), any(Integer.class)))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)));

        verify(userService, times(1)).updateUser(any(UserDto.class), any(Integer.class));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(any(Integer.class));
    }

    @Test
    public void updateUserTest2() throws Exception {
        Integer userId = 1;
        UserDto userDto = createTestUserDto(userId);
        userDto.setName("updatedName");

        when(userService.updateUser(any(UserDto.class), any(Integer.class)))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userDto)))
                .andDo(print());  // Добавлено для вывода информации о запросе и ответе

        verify(userService, times(1)).updateUser(any(UserDto.class), any(Integer.class));
    }

    @Test
    public void deleteUserByIdTest2() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1);
    }

    private UserDto createTestUserDto(Integer id) {
        String name = "user";
        String email = "user@user.com";

        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    @Test
    @SneakyThrows
    void deleteUser_whenUserIdIsValid_thenReturnedStatusOk() {
        int userId = 1;

        mvc
                .perform(
                        delete(CONTROLLER_URL + "/{userId}", userId)
                )
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenUserIdIsNotValid_thenReturnedBadRequest() {
        String userId = "string";

        mvc
                .perform(
                        delete(CONTROLLER_URL + "/{userId}", userId)
                )
                .andExpect(status().isBadRequest());
    }

    private String toJson(UserDto dto) {
        return String.format("{\"id\":%d,\"name\": %s,\"email\": %s}", dto.getId(), dto.getName(), dto.getEmail());
    }
}