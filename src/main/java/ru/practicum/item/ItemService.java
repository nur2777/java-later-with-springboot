package ru.practicum.item;

import java.util.List;
import java.util.Set;

public interface ItemService {

    List<ItemDTO> getItems(Long userId);

    ItemDTO addNewItem(Long userId, ItemDTO itemDTO);

    void deleteItem(Long userId, Long itemId);

    List<ItemDTO> getItems(long userId, Set<String> tags);

    List<ItemDTO> getItems(GetItemRequest req);

    ItemDTO changeItem(long userId, ModifyItemRequest changeItemDTO);
}
