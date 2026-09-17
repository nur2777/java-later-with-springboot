package ru.practicum.itemNote;

import ru.practicum.item.Item;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItemNoteMapper {

    public static ItemNoteDto toItemNoteDto(ItemNote itemNote) {
        String dateOfNote = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(itemNote.getDateOfNote());

        ItemNoteDto itemNoteDto =new ItemNoteDto();
        itemNoteDto.setId(itemNote.getId());
        itemNoteDto.setItemId(itemNote.getItem().getId());
        itemNoteDto.setItemUrl(itemNote.getItem().getUrl());
        itemNoteDto.setDateOfNote(dateOfNote);
        itemNoteDto.setText(itemNote.getNoteText());

        return itemNoteDto;
    }

    public static List<ItemNoteDto> mapToItemNoteDto(Iterable<ItemNote> itemNotes) {
        List<ItemNoteDto> dtos = new ArrayList<>();
        for (ItemNote itemNote : itemNotes) {
            dtos.add(toItemNoteDto(itemNote));
        }
        return dtos;
    }

    public static ItemNote fromItemNoteDto(ItemNoteDto itemNoteDto, Item item) {
        ItemNote itemNote = new ItemNote();
        itemNote.setNoteText(itemNote.getNoteText());
        itemNote.setItem(item);

        return itemNote;
    }
}
