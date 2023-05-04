package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.UserCreate;
import ru.practicum.shareit.user.validation.UserUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank (groups = UserCreate.class)
    private String name;
    @NotNull (groups = UserCreate.class)
    @Email (groups = {UserCreate.class, UserUpdate.class})
    private String email;
}
