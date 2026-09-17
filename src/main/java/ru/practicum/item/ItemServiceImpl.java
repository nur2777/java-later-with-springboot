package ru.practicum.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InsufficientPermissionException;
import ru.practicum.exception.LaterApplicationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.enums.ContentType;
import ru.practicum.item.enums.SortEnum;
import ru.practicum.item.enums.StateEnum;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UrlMetaDataRetriever urlMetaDataRetriever;

    @Override
    public List<ItemDTO> getItems(Long userId) {
        if (!(userId == null)) {
            return itemRepository.findByUserId(userId).stream()
                    .map(ItemDTO::fromItem)
                    .toList();
        } else {
            throw new IllegalArgumentException("Не указан параметр userId");
        }
    }

    @Override
    @Transactional
    public ItemDTO addNewItem(Long userId, ItemDTO itemDTO) {
        if (userId == null) {
            throw new IllegalArgumentException("При создании ссылки не указан параметр userId");
        }
        if (itemDTO == null || itemDTO.getNormalUrl() == null) {
            throw new IllegalArgumentException("При создании ссылки не передано значение ссылки");
        }
        UrlMetaDataRetriever.UrlMetadata result = urlMetaDataRetriever.retrieve(itemDTO.getNormalUrl());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new LaterApplicationException("Указанного пользователя не существует"));
        Optional<Item> maybeExistingItem = itemRepository.findByUserAndResolvedUrl(user, result.getResolvedUrl());
        Item item;
        if(maybeExistingItem.isEmpty()) {
            log.info("TEST: "+ItemDTO.toItem(itemDTO,result,userId).toString());
            item = itemRepository.save(ItemDTO.toItem(itemDTO,result,userId));
        } else {
            item = maybeExistingItem.get();
            if(itemDTO.getTags() != null && !itemDTO.getTags().isEmpty()) {
                item.getTags().addAll(itemDTO.getTags());
                itemRepository.save(item);
            }
        }
        return ItemDTO.fromItem(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        if (userId == null) {
            throw new IllegalArgumentException("При удалении ссылки не указан параметр userId");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("При удалении ссылки не указана ссылка itemId");
        }
        Optional<Item> maybeItem = getAndCheckPermissions(userId, itemId);
        itemRepository.deleteByUserIdAndId(userId,itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getItems(long userId, Set<String> tags) {
        BooleanExpression byUserId = QItem.item.user.id.eq(userId);
        BooleanExpression byAnyTag = QItem.item.tags.any().in(tags);
        Iterable<Item> foundItems = itemRepository.findAll(byUserId.and(byAnyTag));
        List<Item> itemList = StreamSupport.stream(foundItems.spliterator(), false)
                .toList();
        return itemList.stream().map(ItemDTO::fromItem)
                .toList();
    }


    public List<ItemDTO> getItems(GetItemRequest req) {

        QItem item = QItem.item;
        // Объект, в который будем собирать все условия для поиска в базе данных.
        List<BooleanExpression> conditions = new ArrayList<>();
        // Добавить в conditions условие: ищем только ссылки, добавленные текущим пользователем.
        conditions.add(item.user.id.eq(req.getUserId()));
        // Для каждого возможного параметра запроса, если пользователь его передал, добавить соответствующее
        // условие в conditions.
        if (req.getState() != null && !req.getState().equals(StateEnum.ALL)) {
            conditions.add(makeStateCondition(req.getState()));
        }

        if (req.getContentType() != null && !req.getContentType().equals(ContentType.ALL)) {
            conditions.add(makeContentTypeCondition(req.getContentType()));
        }

        if (req.getTags() != null && !req.getTags().isEmpty()) {
            conditions.add(item.tags.any().in(req.getTags()));
        }
        //        Собрать из conditions одно итоговое условие.
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        // Добавить сортировку и пагинацию в соответствии с параметрами, переданными пользователем.
        Sort sort = makeOrderByClause(req.getSort());
        PageRequest pageRequest = PageRequest.of(0, req.getLimit(), sort);
        // Выполнить запрос к базе данных.
        Iterable<Item> items = itemRepository.findAll(finalCondition, pageRequest);
        // Вернуть результат.
        return ItemDTO.mapToListItemDto(items);
    }

    @Override
    public ItemDTO changeItem(long userId, ModifyItemRequest changeItemDTO) {
        Optional<Item> maybeItem = getAndCheckPermissions(userId, changeItemDTO.getItemId());
        if(maybeItem.isPresent()) {
            Item item = maybeItem.get();
            item.setUnread(!changeItemDTO.isRead());
            if(changeItemDTO.isReplaceTags()) {
                item.getTags().clear();
            }
            if(changeItemDTO.hasTags()) {
                item.getTags().addAll(changeItemDTO.getTags());
            }
            item = itemRepository.save(item);
            return ItemDTO.fromItem(item);
        } else {
            throw new NotFoundException("Не найдена закладка itemId " + changeItemDTO.getItemId());
        }
    }

    private Optional<Item> getAndCheckPermissions(long userId, long itemId) {
        Optional<Item> maybeItem = itemRepository.findById(itemId);
        if (maybeItem.isPresent()) {
            Item item = maybeItem.get();
            if(!item.getUser().getId().equals(userId)) {
                throw new InsufficientPermissionException("Вы не имеете права выполнять эту операцию");
            }
        }
        return maybeItem;
    }

    private BooleanExpression makeStateCondition(StateEnum state) {
        switch (state) {
            case READ -> {
                return QItem.item.unread.isFalse();
            }
            case UNREAD -> {
                return QItem.item.unread.isTrue();
            }
            default -> {
                return null;
            }
        }
    }

    private BooleanExpression makeContentTypeCondition(ContentType contentType) {
        switch (contentType) {
            case VIDEO -> {
                return QItem.item.mimeType.eq("video");
            }
            case ARTICLE -> {
                return QItem.item.mimeType.eq("text");
            }
            case IMAGE -> {
                return QItem.item.mimeType.eq("image");
            }
            default -> {
                return null;
            }
        }
    }

    private Sort makeOrderByClause(SortEnum sort) {
        switch (sort) {
            case TITLE: return Sort.by("title").ascending();
            case OLDEST: return Sort.by("dateResolved").ascending();
            default: return Sort.by("dateResolved").descending();
        }
    }

}
