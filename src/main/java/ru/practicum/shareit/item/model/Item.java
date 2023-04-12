package ru.practicum.shareit.item.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
}
