package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest itemRequest;

    public Item(long id, String name, String description, Boolean available, long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = userId;
    }
}
