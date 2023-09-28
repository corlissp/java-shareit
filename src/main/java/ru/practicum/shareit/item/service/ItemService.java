package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Min Danil 28.09.2023
 */
@Service
public class ItemService {
    private final ItemRepository repository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Autowired
    public ItemService(ItemRepository repository, ItemMapper itemMapper, UserService userService) {
        this.repository = repository;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    public List<ItemDto> getAllItems(Integer userId) {

        UserDto userDto = userService.getUser(userId);

        return repository.getAllItems(userDto.getId()).stream().map(item -> {
            ItemDto itemDTO = itemMapper.toDto(item);
            itemDTO.setOwner(userDto.getId());
            return itemDTO;
        }).collect(Collectors.toList());
    }

    public ItemDto getItemById(Integer itemId) {
        Item item = repository.getItemById(itemId);

        ItemDto itemDTO = itemMapper.toDto(item);
        itemDTO.setOwner(item.getOwner());

        return itemDTO;
    }

    public ItemDto saveItem(Integer userId, ItemDto itemDTO) {
        UserDto userDto = userService.getUser(userId);
        itemDTO.setOwner(userDto.getId());
        Item item = repository.saveItem(userId, itemMapper.toItem(itemDTO));
        return itemMapper.toDto(item);
    }

    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto itemDTO) {
        Item item = repository.getItemById(itemId);

        checkOwner(userId, item);

        return itemMapper.toDto(repository.updateItem(itemId, itemMapper.toItem(itemDTO)));
    }

    public List<ItemDto> searchAvailableItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchAvailableItemsByText(text).stream().map(item -> {
            ItemDto itemDTO = itemMapper.toDto(item);
            itemDTO.setOwner(item.getOwner());
            return itemDTO;
        }).collect(Collectors.toList());
    }


    public void deleteItem(Integer userId, Integer itemId) {
        Item item = repository.getItemById(itemId);
        checkOwner(userId, item);
        repository.deleteItem(itemId);
    }

    private void checkOwner(Integer userId, Item item) {
        if (item.getOwner().equals(userId)) {
            throw new NotFoundItemException("Не найден предмет у пользователя!");
        }
    }
}
