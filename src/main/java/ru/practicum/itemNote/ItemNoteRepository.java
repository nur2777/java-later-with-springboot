package ru.practicum.itemNote;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemNoteRepository extends JpaRepository<ItemNote,Long> {

    List<ItemNote> findAllByItemUserIdAndItemUrlContainingIgnoreCase(Long userId, String url);

    @Query("select itn " +
            "from ItemNote as itn " +
            "join itn.item as i " +
            "where i.user.id = ?1 " +
                "and ?2 member of i.tags "
    )
    List<ItemNote> findByItemUserIdAndTags(Long userId, String tag);

    List<ItemNote> findAllByItemUserId(Long userId, Pageable page);
}
