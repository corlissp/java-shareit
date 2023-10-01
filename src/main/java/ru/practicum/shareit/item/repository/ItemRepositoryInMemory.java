package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Min Danil 28.09.2023
 */
@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private static final String NOT_FOUND_ITEM = "Не найден предмет с id = ";
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Integer>> ownerItems = new HashMap<>();
    private int freeId = 1;

    @Override
    public List<Item> getAllItems(Integer userId) {
        return ownerItems.getOrDefault(userId, new ArrayList<>()).stream()
                .map(items::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Integer userId, Item item) {
        item.setId(getFreeId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        ownerItems.merge(userId, new ArrayList<>(List.of(item.getId())), (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        });
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Integer id) {
        if (!items.containsKey(id)) {
            throw new NotFoundItemException(NOT_FOUND_ITEM + id);
        }
        return items.get(id);
    }

    @Override
    public Item updateItem(Integer itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundItemException(NOT_FOUND_ITEM + itemId);
        }
        Item oldItem = items.get(itemId);
        Item newItem = Item.builder()
                .id(oldItem.getId())
                .owner(Objects.requireNonNullElse(item.getOwner(),oldItem.getOwner()))
                .request(oldItem.getRequest())
                .name(Objects.requireNonNullElse(item.getName(), oldItem.getName()))
                .description(Objects.requireNonNullElse(item.getDescription(), oldItem.getDescription()))
                .available(Objects.requireNonNullElse(item.getAvailable(), oldItem.getAvailable()))
                .build();

        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public void deleteItem(Integer itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundItemException(NOT_FOUND_ITEM + itemId);
        }
        items.remove(itemId);
    }

    @Override
    public List<Item> searchAvailableItemsByText(String text) {
        return items.values().stream().filter(Item::getAvailable).filter(item ->
                item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
        ).collect(Collectors.toList());
    }

    private int getFreeId() {
        return freeId++;
    }
}
