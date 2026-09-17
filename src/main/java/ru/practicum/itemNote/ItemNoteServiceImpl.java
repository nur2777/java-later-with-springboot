package ru.practicum.itemNote;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemNoteServiceImpl implements ItemNoteService{

    private final ItemNoteRepository itemNoteRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemNoteDto addNewItemNote(long userId, ItemNoteDto itemNoteDto) {
        Item item = itemRepository.findById(itemNoteDto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        ItemNote itemNote = ItemNoteMapper.fromItemNoteDto(itemNoteDto,item);
        return ItemNoteMapper.toItemNoteDto(itemNoteRepository.save(itemNote));
    }

    @Override
    public List<ItemNoteDto> searchNotesByUrl(String url, Long userId) {
        return itemNoteRepository.findAllByItemUserIdAndItemUrlContainingIgnoreCase(userId,url).stream()
                .map(itemNote -> ItemNoteMapper.toItemNoteDto(itemNote))
                .toList();
    }

    @Override
    public List<ItemNoteDto> searchNotesByTag(long userId, String tag) {
        return itemNoteRepository.findByItemUserIdAndTags(userId,tag)
                .stream()
                .map(itemNote -> ItemNoteMapper.toItemNoteDto(itemNote))
                .toList();
    }

    @Override
    public List<ItemNoteDto> listAllItemsWithNotes(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemNoteRepository.findAllByItemUserId(userId, page).stream()
                .map(itemNote -> ItemNoteMapper.toItemNoteDto(itemNote))
                .toList();
    }
}
