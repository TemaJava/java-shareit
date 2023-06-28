package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available is true")
    List<Item> findAllByString(String str);

    List<Item> findAllByUserIdOrderByIdAsc(long userId);

    @Query("select item from Item item " +
            "where item.request.id in :ids")
    List<Item> searchByRequestsId(@Param("ids") List<Long> ids);
}
