package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.enums.ContentType;
import ru.practicum.item.enums.SortEnum;
import ru.practicum.item.enums.StateEnum;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDTO> get(
            @RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "UNREAD") String state,
            @RequestParam(name = "contentType", defaultValue = "ALL") String contentType,
            @RequestParam(name = "sort", defaultValue = "NEWEST") String sort,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "tags", required = false) List<String> tags
    ) {
        GetItemRequest getItemRequest = new GetItemRequest();
        getItemRequest.setUserId(userId);
        if (state != null) {
            getItemRequest.setState(StateEnum.valueOf(state));
        }
        if (contentType != null) {
            getItemRequest.setContentType(ContentType.valueOf(contentType));
        }
        if (tags !=null) {
            getItemRequest.setTags(tags);
        }
        if (sort != null) {
            getItemRequest.setSort(SortEnum.valueOf(sort));
        }
        getItemRequest.setLimit(limit);
        return itemService.getItems(getItemRequest);
    }

    @PostMapping
    public ItemDTO add(@RequestHeader("X-Later-User-Id") Long userId,
                    @RequestBody ItemDTO itemDTO) {
        return itemService.addNewItem(userId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable(name="itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping
    public ItemDTO changeItem(@RequestHeader("X-Later-User-Id") long userId,
                              @RequestBody ModifyItemRequest changeItemDTO) {
        return itemService.changeItem(userId, changeItemDTO);
    }
}