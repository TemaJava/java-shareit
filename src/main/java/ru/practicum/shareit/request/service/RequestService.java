package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoToResponse;

import java.util.List;


public interface RequestService {
    RequestDto createRequest(RequestDto dto, long userId);

    List<RequestDtoToResponse> getRequests(long userId);

    List<RequestDtoToResponse> getOtherUserRequests(long userId, Pageable pageable);

    RequestDtoToResponse getRequestById(long requestId, long userId);
}
