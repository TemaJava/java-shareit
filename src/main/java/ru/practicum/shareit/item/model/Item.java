package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ITEMS")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}