package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Validated(Create.class) @RequestBody RequestDto dto) {
        return requestService.createRequest(dto, userId);
    }

    @GetMapping
    public List<RequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDtoResponse> getOtherRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size) {
        return requestService.getOtherUserRequests(userId, Pagination.toPageable(from, size));
    }

    @GetMapping("/{requestId}")
    public RequestDtoResponse getById(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestById(requestId, userId);
    }
}
