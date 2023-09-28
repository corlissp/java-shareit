package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * @author Min Danil 28.09.2023
 */
public interface ItemRepository {
    List<Item> getAllItems(Integer userId);

    Item saveItem(Integer userId, Item item);

    Item getItemById(Integer id);

    Item updateItem(Integer itemId, Item item);

    void deleteItem(Integer itemId);

    List<Item> searchAvailableItemsByText(String text);
}
