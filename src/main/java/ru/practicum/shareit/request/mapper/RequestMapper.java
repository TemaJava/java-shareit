package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;


@UtilityClass
public class RequestMapper {
    public static Request toRequest(RequestDto itemRequestDto, User user) {
        return new Request(itemRequestDto.getId(),
                user,
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated());
    }

    public static RequestDto toRequestDto(Request itemRequest) {
        return new RequestDto(itemRequest.getId(),
                itemRequest.getUser().getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public static RequestDtoResponse toRequestDtoResponse(Request itemRequest) {
        return new RequestDtoResponse(itemRequest.getId(),
                itemRequest.getUser().getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }
}
