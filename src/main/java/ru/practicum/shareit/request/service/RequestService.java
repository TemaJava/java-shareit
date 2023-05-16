package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;


public interface RequestService {
    RequestDto createRequest(RequestDto dto, long userId);

    List<RequestDtoResponse> getRequests(long userId);

    List<RequestDtoResponse> getOtherUserRequests(long userId, Pageable pageable);

    RequestDtoResponse getRequestById(long requestId, long userId);
}
