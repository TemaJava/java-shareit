package ru.practicum.shareit.requestTests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoToResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;

import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @InjectMocks
    private RequestServiceImpl itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime now = LocalDateTime.now();

    private User user;
    private Request request;

    @BeforeEach
    void createModels() {
        user = new User(1L, "UserName", "mail@mail.ru");
        request = new Request(1L, user, "test desc", now);
    }

    @Test
    void createRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);

        RequestDto requestTestDto = RequestMapper.toRequestDto(request);
        RequestDto requestResponseDto =
                itemRequestService.createRequest(RequestMapper.toRequestDto(request), user.getId());
        assertEquals(requestTestDto.getId(), requestResponseDto.getId());
    }

    @Test
    void getOwnRequestsWithEmptyRequestsShouldReturnEmptyList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        List<RequestDtoToResponse> responseList = itemRequestService.getRequests(user.getId());
        assertTrue(responseList.isEmpty());
    }

    @Test
    void getOwnRequestsShouldReturnListOfRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Request> requests = List.of(
                request,
                new Request(2L, user, "decs2", now)
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(anyLong())).thenReturn(requests);

        List<RequestDtoToResponse> responseList = itemRequestService.getRequests(user.getId());
        assertEquals("test desc", responseList.get(0).getDescription());
        assertEquals(2, responseList.get(1).getId());
    }

    @Test
    void getOtherRequestsShouldReturnListOfRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));

        List<RequestDtoToResponse> itemRequestDtos = itemRequestService.getOtherUserRequests(
                user.getId(),
                PageRequest.of(10, 10));
        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals(user.getId(), itemRequestDtos.get(0).getUserId());
        assertEquals(Collections.emptyList(), itemRequestDtos.get(0).getItems());
    }

    @Test
    void getByIdShouldReturnRequest() {
        RequestDto requestDto = RequestMapper.toRequestDto(request);

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        RequestDtoToResponse request1 = itemRequestService.getRequestById(requestDto.getId(), user.getId());
        assertEquals(request1.getId(), requestDto.getId());
        assertEquals(request1.getDescription(), requestDto.getDescription());
    }
}
