package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    public static final Integer ID = 1;
    public static final String TEXT_PARAM = "text";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final LocalDateTime CREATION_DATE = LocalDateTime.now();


    @MockBean
    ItemService itemService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MockMvc mvc;
    @InjectMocks
    private ItemController itemController;

    @Autowired
    public ItemControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void createItemTest() throws Exception {
        ItemDto inputDto = generateItemInputDto();
        ItemDto responseDto = generateItemResponseDto(ID, inputDto);

        when(itemService.saveItem(any(Integer.class), any(ItemDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(responseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription()), String.class));

        verify(itemService, times(1))
                .saveItem(any(Integer.class), any(ItemDto.class));
    }

    @Test
    public void createItemBadRequestTest() throws Exception {
        ItemDto inputDto = generateItemInputEmptyDto();
        ItemDto responseDto = generateItemResponseDto(ID, inputDto);

        when(itemService.saveItem(any(Integer.class), any(ItemDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCommentTest() throws Exception {
        CreateCommentDto inputCommentDto = new CreateCommentDto("text");
        CommentDto responseCommentDto = generateResponseCommentDto(ID, inputCommentDto);

        when(itemService.saveComment(any(CreateCommentDto.class), any(Integer.class), any(Integer.class)))
                .thenReturn(responseCommentDto);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText()), String.class));

        verify(itemService, times(1))
                .saveComment(any(CreateCommentDto.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto inputDto = generateItemInputDto();
        inputDto.setName("updatedName");
        ItemDto responseDto = generateItemResponseDto(ID, inputDto);

        when(itemService.updateItem(any(Integer.class), any(Integer.class), any(ItemDto.class)))
                .thenReturn(responseDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(responseDto.getName()), String.class));

        verify(itemService, times(1))
                .updateItem(any(Integer.class), any(Integer.class), any(ItemDto.class));
    }

    @Test
    public void findItemByIdTest() throws Exception {
        ItemDto responseDto = generateItemResponseDto(ID, generateItemInputDto());

        when(itemService.getItemById(any(Integer.class), any(Integer.class)))
                .thenReturn(responseDto);

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class));

        verify(itemService, times(1)).getItemById(any(Integer.class), any(Integer.class));
    }

    @Test
    public void getAllItemsTest() throws Exception {
        List<ItemDto> itemList = List.of(generateItemResponseDto(ID, generateItemInputDto()));

        when(itemService.getAllItems(any(Integer.class)))
                .thenReturn(itemList);

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(ID), Integer.class));

        verify(itemService, times(1)).getAllItems(any(Integer.class));
    }

    @Test
    public void deleteItemTest() throws Exception {
        doNothing().when(itemService).deleteItem(any(Integer.class), any(Integer.class));

        mvc.perform(delete("/items/1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(any(Integer.class), any(Integer.class));
    }

    @Test
    public void searchAvailableItemsTest() throws Exception {
        String searchText = "text";
        List<ItemDto> itemList = List.of(generateItemResponseDto(ID, generateItemInputDto()));

        when(itemService.searchAvailableItemsByText(any(String.class)))
                .thenReturn(itemList);

        mvc.perform(get("/items/search")
                        .param(TEXT_PARAM, searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(ID), Integer.class));

        verify(itemService, times(1)).searchAvailableItemsByText(any(String.class));
    }


    private CommentDto generateResponseCommentDto(Integer id, CreateCommentDto dto) {
        return CommentDto.builder()
                .id(id)
                .authorName("name")
                .text(dto.getText())
                .created(CREATION_DATE)
                .build();
    }

    private ItemDto generateItemInputDto() {
        return ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    private ItemDto generateItemInputEmptyDto() {
        return ItemDto.builder()
                .name("")
                .description("")
                .available(true)
                .build();
    }

    private ItemDto generateItemResponseDto(Integer id, ItemDto inputDto) {
        return ItemDto.builder()
                .id(id)
                .name(inputDto.getName())
                .description(inputDto.getDescription())
                .available(inputDto.getAvailable())
                .build();
    }
}