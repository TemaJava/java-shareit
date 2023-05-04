package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(unique = true)
    private String email;
}
