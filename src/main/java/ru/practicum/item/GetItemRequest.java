package ru.practicum.item;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.item.enums.ContentType;
import ru.practicum.item.enums.SortEnum;
import ru.practicum.item.enums.StateEnum;

import java.util.List;

@Data
@NoArgsConstructor
public class GetItemRequest {
    Long userId;
    StateEnum state;
    ContentType contentType;
    List<String> tags;
    SortEnum sort;
    Integer limit;
}
