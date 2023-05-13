package ru.practicum.shareit.bookingTests.bookingControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private UserDto userDto;
    private BookingDto bookingDto;
    private BookingDtoToResponse bookingDtoResponse;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime bookingStart = now.plusDays(1);
    LocalDateTime bookingEnd = now.plusDays(2);

    @BeforeEach
    void createModel() {
        User user = new User(1L, "User name", "user@mail.com");
        User anotherUser = new User(2L, "Another name", "another@mail.com");
        userDto = UserMapper.toUserDto(user);

        Item item = new Item(1, "itemName", "itemDesc", true, user, null);
        Booking booking = new Booking(1L, bookingStart, bookingEnd, item, anotherUser, Status.WAITING);
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDtoResponse = BookingMapper.toBookingDtoToResponse(booking);
    }

    @Test
    void create() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));

    }

    @Test
    void changeStatus() throws Exception {
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));


    }

    @Test
    void getInfo() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", userDto.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    void getByBooker() throws Exception {
        when(bookingService.getBookingByBooker(anyLong(), any(String.class), any()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoResponse))));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getBookingByOwner(anyLong(), any(String.class), any()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoResponse))));
    }
}
