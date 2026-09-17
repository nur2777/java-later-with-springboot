package ru.practicum.item;

import lombok.*;
import ru.practicum.user.User;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ItemDTO {
    /**
     * Идентификатор
     */
    private Long id;
    /**
     * Идентификатор пользователя
     */
    private Long userId;

    private String normalUrl;
    private String resolvedUrl;
    private String mimeType;
    private String title;
    private boolean hasImage;
    private boolean hasVideo;
    private boolean unread;
    private String dateResolved;
    /**
     * Список уникальных тегов
     */
    private Set<String> tags;


    /** Маппер из модели в DTO в виде статического метода
     * @param item сущность БД
     * @return DTO объект
     */
    public static ItemDTO fromItem(Item item) {
        final DateTimeFormatter dtFormatter = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.id = item.getId();
        itemDTO.userId = item.getUser().getId();

        itemDTO.title = item.getTitle();
        itemDTO.normalUrl = item.getUrl();
        itemDTO.resolvedUrl = item.getResolvedUrl();
        itemDTO.hasImage = item.isHasImage();
        itemDTO.hasVideo = item.isHasVideo();
        itemDTO.mimeType = item.getMimeType();
        itemDTO.unread = item.isUnread();
        itemDTO.title = item.getTitle();
        itemDTO.dateResolved = dtFormatter.format(item.getDateResolved());
        itemDTO.tags = item.getTags();
        itemDTO.normalUrl = item.getUrl();
        return  itemDTO;
    }

    /** Маппер из DTO в модель в виде статического метода
     * @param itemDto DTO объект
     * @return модель данных
     */
    public static Item toItem(ItemDTO itemDto, UrlMetaDataRetriever.UrlMetadata urlMetadata,Long userId) {
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setUser(user);
        item.setTags(itemDto.getTags());
        item.setUrl(urlMetadata.getNormalUrl());
        item.setResolvedUrl(urlMetadata.getResolvedUrl());
        item.setMimeType(urlMetadata.getMimeType());
        item.setTitle(urlMetadata.getTitle());
        item.setHasImage(urlMetadata.isHasImage());
        item.setHasVideo(urlMetadata.isHasVideo());
        item.setDateResolved(urlMetadata.getDateResolved());
        return  item;
    }

    public static List<ItemDTO> mapToListItemDto(Iterable<Item> items) {
        List<ItemDTO> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(fromItem(item));
        }
        return dtos;
    }
}