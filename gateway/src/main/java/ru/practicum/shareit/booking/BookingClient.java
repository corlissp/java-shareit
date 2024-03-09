package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    public static final String BOOKING_INVALID_MESSAGE = "недопустимые значения времени бронирования: ";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingPostDto dto, Integer userId) {
        if (!isStartBeforeEnd(dto)) {
            throw new IllegalArgumentException(BOOKING_INVALID_MESSAGE +
                    "start: " + dto.getStart() + " end: " + dto.getEnd() + " now: ");
        }
        return post("", userId, dto);
    }

    public ResponseEntity<Object> patchBooking(Long bookingId, Boolean approved, Integer userId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> findById(Integer bookingId, Integer userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByBooker(String state, Integer userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllByItemOwner(String state, Integer userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private boolean isStartBeforeEnd(BookingPostDto dto) {
        return dto.getStart().isBefore(dto.getEnd());
    }
}