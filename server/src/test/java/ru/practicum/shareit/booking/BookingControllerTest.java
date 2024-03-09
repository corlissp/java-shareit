package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    public static final Integer ID = 1;
    public static final String FROM_VALUE = "0";
    public static final String SIZE_VALUE = "20";
    public static final String FROM_PARAM = "from";
    public static final String SIZE_PARAM = "size";
    public static final String STATE_VALUE = "ALL";
    public static final String STATE_PARAM = "state";
    public static final String APPROVED_VALUE = "true";
    public static final String APPROVED_PARAM = "approved";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final LocalDateTime START_DATE = LocalDateTime.now();
    public static final LocalDateTime END_DATE = START_DATE.plusDays(7);

    @MockBean
    BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @Test
    public void createBookingTest() throws Exception {
        BookingPostDto inputDto = generateInputDto();
        BookingPostResponseDto responseDto = generatePostResponseDto(ID, inputDto);

        when(bookingService.createBooking(any(BookingPostDto.class), any(Integer.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()),  Integer.class))
                .andExpect(jsonPath("$.item", is(responseDto.getItem()),  Item.class));

        verify(bookingService, times(1))
                .createBooking(any(BookingPostDto.class), any(Integer.class));
    }

    @Test
    public void patchBookingTest() throws Exception {
        BookingResponseDto responseDto = generateResponseDto(ID);

        when(bookingService.patchBooking(any(Integer.class), any(Boolean.class), any(Integer.class)))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class));

        verify(bookingService, times(1))
                .patchBooking(any(Integer.class), any(Boolean.class), any(Integer.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        BookingDetailedDto responseDto = generateDetailedDto(ID);

        when(bookingService.findById(any(Integer.class), any(Integer.class)))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class));

        verify(bookingService, times(1))
                .findById(any(Integer.class), any(Integer.class));
    }

    @Test
    public void findAllBookingsTest() throws Exception {
        when(bookingService.findAllByBooker(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
                        .param(STATE_PARAM, STATE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .findAllByBooker(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findAllTest() throws Exception {
        when(bookingService
                .findAllByItemOwner(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .findAllByItemOwner(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class));
    }

    private BookingPostDto generateInputDto() {
        return BookingPostDto.builder()
                .id(ID)
                .build();
    }

    private BookingDetailedDto generateDetailedDto(Integer id) {
        return BookingDetailedDto.builder()
                .id(id)
                .name("name")
                .booker(new User())
                .item(new Item())
                .start(START_DATE)
                .end(END_DATE)
                .build();
    }

    private BookingPostResponseDto generatePostResponseDto(Integer id, BookingPostDto inputDto) {
        Item item = new Item();
        item.setId(ID);
        item.setName("item");
        item.setDescription("item description");
        item.setAvailable(true);
        return BookingPostResponseDto.builder()
                .id(id)
                .item(new Item())
                .start(START_DATE)
                .end(END_DATE)
                .build();
    }

    private BookingResponseDto generateResponseDto(Integer id) {
        return BookingResponseDto.builder()
                .id(id)
                .name("Item name")
                .booker(new User())
                .item(new Item())
                .build();
    }
}