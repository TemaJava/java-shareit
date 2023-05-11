package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoToResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestStorage;
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    @Override
    public RequestDto createRequest(RequestDto requestDto, long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        requestDto.setCreated(LocalDateTime.now());
        Request itemRequest = requestStorage.save(RequestMapper.toRequest(requestDto, user));
        return RequestMapper.toRequestDto(itemRequest);
    }

    @Override
    public List<RequestDtoToResponse> getRequests(long userId) {
        userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<RequestDtoToResponse> list = requestStorage.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(RequestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
        return setItemsToRequests(list);
    }

    @Override
    public List<RequestDtoToResponse> getOtherUserRequests(long userId, Pageable pageable) {
        userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<RequestDtoToResponse> list = requestStorage.findAllByUserIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(RequestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
        return setItemsToRequests(list);
    }

    @Override
    public RequestDtoToResponse getRequestById(long requestId, long userId) {
        userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Request request = requestStorage.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));

        return setItemsToRequests(List.of(RequestMapper.toRequestDtoResponse(request))).get(0);
    }

    private List<RequestDtoToResponse> setItemsToRequests(List<RequestDtoToResponse> itemRequestDtoResponseList) {
        Map<Long, RequestDtoToResponse> requests = itemRequestDtoResponseList.stream()
                .collect(Collectors.toMap(RequestDtoToResponse::getId, film -> film, (a, b) -> b));
        List<Long> ids = requests.values().stream()
                .map(RequestDtoToResponse::getId)
                .collect(Collectors.toList());
        List<ItemDto> items = itemStorage.searchByRequestsId(ids).stream()
                .map(ItemMapper::createItemDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
        return new ArrayList<>(requests.values());
    }
}
