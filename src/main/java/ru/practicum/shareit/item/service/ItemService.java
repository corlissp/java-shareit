package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Min Danil 28.09.2023
 */
@Service
public class ItemService {
    private static final String NOT_FOUND_USER = "Не найден пользователь с id = ";
    public static final String EMPTY_COMMENT_MESSAGE = "Комментарий не может быть пустым";
    public static final String DENIED_ACCESS_MESSAGE = "Пользователь не является владельцем вещи";
    public static final String COMMENT_EXCEPTION_MESSAGE = "Нельзя оставить комментарий на вещь, " +
            "который вы не пользовались или ещё не закончился срок аренды";
    private static final String NOT_FOUND_ITEM = "Не найден item с id = ";
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Autowired
    public ItemService(ItemRepository repository, CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository, BookingRepository bookingRepository, ItemMapper itemMapper, UserService userService) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    public List<ItemDto> getAllItems(Integer userId) {
//        UserDto userDto = userService.getUser(userId);
//        return repository.findAll().stream()
//                .filter(item -> item.getOwner().equals(userId))
//                .map(item -> {
//            ItemDto itemDTO = itemMapper.toDto(item);
//            itemDTO.setOwner(userDto.getId());
//            return itemDTO;})
//                .collect(Collectors.toList());
        List<Item> userItems = repository.findAll()
                .stream().filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());

        List<ItemDto> result = new ArrayList<>();
        fillItemDtoList(result, userItems, userId);

        result.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return 0;
            }
            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null && o2.getNextBooking() != null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return result;
    }

    private void fillItemDtoList(List<ItemDto> targetList, List<Item> foundItems, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        Sort sortDesc = Sort.by("start").descending();

        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            if (item.getOwner().equals(userId)) {
                ItemDto dto = constructItemDtoForOwner(item, now, sortDesc, comments);
                targetList.add(dto);
            } else {
                targetList.add(itemMapper.toDto(item, comments));
            }
        }
    }

    private ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, Sort sort, List<Comment> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now, sort)
                .stream()
                .findFirst()
                .orElse(null);
        sort = sort.ascending();
        Booking nextBooking = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now, sort)
                .stream()
                .findFirst()
                .orElse(null);
        return itemMapper.toDto(item,
                lastBooking,
                nextBooking,
                comments);
    }

    public ItemDto getItemById(Integer itemId, Integer userId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM + itemId));

        List<Comment> comments = commentRepository.findByItemId(itemId);

        if (item.getOwner().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Sort sortDesc = Sort.by("start").descending();
            return constructItemDtoForOwner(item, now, sortDesc, comments);
        }
//        ItemDto itemDTO = itemMapper.toDto(item);
//        itemDTO.setOwner(item.getOwner());
//        itemDTO.setComments(commentMapper.toCommentDtoList(comments));
        //return itemDTO;
        return itemMapper.toDto(item, null, null, comments);
    }

    public ItemDto saveItem(Integer userId, ItemDto itemDTO) {
        UserDto userDto = userService.getUser(userId);
        itemDTO.setOwner(userDto.getId());
        Item item = repository.save(itemMapper.toItem(itemDTO));
        return itemMapper.toDto(item);
    }

    public CommentDto saveComment(CreateCommentDto commentDto, Integer itemId, Integer userId) {
        if (commentDto.getText().isBlank()) throw new CommentException(EMPTY_COMMENT_MESSAGE);
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM + itemId));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException(COMMENT_EXCEPTION_MESSAGE + " itemId: " + itemId);
        }
        Comment comment = commentMapper.toComment(commentDto, item, author);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
        // return null;
    }

    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto itemDTO) {
//        Item item = repository.findById(itemId).orElseThrow();
//        checkOwner(userId, item);
//        Item patch = itemMapper.toItem(itemDTO);
//        patch.setId(itemId);
//        item = repository.save(refreshItem(patch));
//        return itemMapper.toDto(item);
//
//        Item item = itemMapper.toItem(itemDTO);
//        item.setId(itemId);
//        item.setOwner(userId);
//        List<Comment> comments = commentRepository.findByItemId(itemId);
//        item = repository.save(refreshItem(item));
//        return itemMapper.toDto(item, comments);

        // User user = userRepository.findById(userId).orElseThrow();
        Item oldItem = repository.findById(itemId).orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM + itemId));
        checkOwner(userId, oldItem);
        Item updatedItem = Item.builder()
                .id(itemId)
                .owner(oldItem.getOwner())
                //.request(oldItem.getRequest())
                .name(Objects.requireNonNullElse(itemDTO.getName(), oldItem.getName()))
                .description(Objects.requireNonNullElse(itemDTO.getDescription(), oldItem.getDescription()))
                .available(Objects.requireNonNullElse(itemDTO.getAvailable(), oldItem.getAvailable()))
                .build();

        repository.save(updatedItem);

        return itemMapper.toDto(updatedItem);
    }

    public List<ItemDto> searchAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.search(text).stream().map(item -> {
            ItemDto itemDTO = itemMapper.toDto(item);
            itemDTO.setOwner(item.getOwner());
            return itemDTO;
        }).collect(Collectors.toList());
    }


    public void deleteItem(Integer userId, Integer itemId) {
        Item item = repository.findById(itemId).orElseThrow();
        checkOwner(userId, item);
        repository.deleteById(itemId);
    }

    private Item refreshItem(Item patch) {
        Item entry = repository.findById(patch.getId()).orElseThrow();
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String description = patch.getDescription();
        if (description != null && !description.isBlank()) {
            entry.setDescription(description);
        }

        Boolean available = patch.getAvailable();
        if (available != null) {
            entry.setAvailable(available);
        }
        return entry;
    }

    private void checkOwner(Integer userId, Item item) {
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundItemException("Не найден предмет у пользователя!");
        }
    }
}
