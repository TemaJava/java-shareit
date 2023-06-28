package ru.practicum.shareit.requestTests.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyLong;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
//потребовалось для создания маппера
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerTest {
    @MockBean
    private RequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper;

    private User user;
    private RequestDtoResponse requestDtoToResponse;
    private RequestDto requestDto;
    private Request request;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void createModels() {
        user = new User(1L, "test name", "test@maiil.ru");
        request = new Request(1L, user, "desc", now);
        requestDto = RequestMapper.toRequestDto(request);
        requestDtoToResponse = RequestMapper.toRequestDtoResponse(request);
    }

    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.createRequest(any(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void getRequestsTest() throws Exception {
        when(itemRequestService.getRequests(anyLong())).thenReturn(Collections.singletonList(requestDtoToResponse));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    void getRequestsListTest() throws Exception {
        when(itemRequestService.getOtherUserRequests(anyLong(), any()))
                .thenReturn(Collections.singletonList(requestDtoToResponse));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDtoToResponse);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("desc"));
    }
}
