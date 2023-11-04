package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * @author Min Danil 04.11.2023
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    public static final Sort SORT = Sort.by("created").descending();
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public PostResponseRequestDto createRequest(PostRequestDto dto, Integer userId) {
        checkIfUserExists(userId);
        Request request  = requestMapper.toModel(dto, userId);
        request = requestRepository.save(request);
        return requestMapper.toPostResponseDto(request);
    }

    public List<RequestWithItemsDto> findAllByUserId(Integer userId) {
        checkIfUserExists(userId);
        List<Request> requests = requestRepository.findRequestByRequestorOrderByCreatedDesc(userId);
        return requestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    public List<RequestWithItemsDto> findAll(int from, int size, Integer userId) {
        checkIfUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT.descending());
        Page<Request> requests = requestRepository.findAll(userId, pageable);
        return requestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    public RequestWithItemsDto findById(Integer requestId, Integer userId) {
        checkIfUserExists(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(""));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return requestMapper.toRequestWithItemsDto(request, items);
    }

    private void checkIfUserExists(Integer userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(""));
    }

}
