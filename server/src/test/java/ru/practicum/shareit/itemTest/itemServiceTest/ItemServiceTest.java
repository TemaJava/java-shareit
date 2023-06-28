package ru.practicum.shareit.itemTest.itemServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    private User user;

    private User anotherUser;

    private Item item;

    private Booking booking;

    private Comment comment;

    private LocalDateTime now = LocalDateTime.now();

    private LocalDateTime bookingStart = now.minusHours(2);

    private LocalDateTime bookingEnd = now.minusHours(1);

    @BeforeEach
    void createModel() {
        user = new User(1L, "user name", "user@mail.ru");
        anotherUser = new User(2L, "Another user name", "another@mail.ru");
        item = new Item(1L, "itemName", "itemDesc", true, user, null);
        booking = new Booking(1L, bookingStart, bookingEnd, item, anotherUser, Status.WAITING);
        comment = new Comment(1L, "commentText", item, anotherUser, now);
    }

    @Test
    void createItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDto = itemService.createItem(item.getId(), ItemMapper.createItemDto(item));

        assertEquals(1, itemDto.getId());
        assertEquals("itemDesc", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }

    @Test
    void createItemWithUnknownUserShouldThrowExceptionTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(1L, ItemMapper.createItemDto(item)));
        assertEquals("Пользователь с id = " + 1 + " не найден", exception.getMessage());
    }

    @Test
    void createItemWithUnknownRequestShouldThrowExceptionTest() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        item.setRequest(new Request(1L, user, "desc", now));
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(1L, ItemMapper.createItemDto(item)));
        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        ItemBookingDto itemDtoBooking = itemService.getItemById(user.getId(), item.getId());

        assertEquals(1, itemDtoBooking.getId());
        assertEquals("itemName", itemDtoBooking.getName());
        assertEquals(true, itemDtoBooking.getAvailable());
    }

    @Test
    void getAllUsersItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByUserIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        List<ItemBookingDto> itemDtoBooking = itemService.getAllUsersItems(user.getId());
        assertEquals(1, itemDtoBooking.get(0).getId());
        assertEquals("itemName", itemDtoBooking.get(0).getName());
        assertEquals("itemDesc", itemDtoBooking.get(0).getDescription());
    }

    @Test
    void getAllUsersItemsWithUnknownUserShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getAllUsersItems(user.getId()));
        assertEquals("Пользователь c id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto itemDto = ItemMapper.createItemDto(item);
        itemDto.setDescription("newDesc");
        ItemDto itemDto1 = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto1.getDescription(), itemDto.getDescription());
        assertEquals(itemDto1.getName(), itemDto.getName());
    }

    @Test
    void updateItemWithUnknownUserShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(user.getId(), item.getId()));
        assertEquals("Пользователь c id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void updateItemWithUnknownItemShouldThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(user.getId(), item.getId()));
        assertEquals("Предмет с id " + item.getId() + " не обнаружен", exception.getMessage());
    }

    @Test
    void findItemByNameInNormalCase() {
        when(itemRepository.findAllByString(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto = itemService.findItem("itemName");

        assertEquals("itemName", itemDto.get(0).getName());
        assertEquals("itemDesc", itemDto.get(0).getDescription());
        assertEquals(true, itemDto.get(0).getAvailable());
    }

    @Test
    void findItemByNameInRandomCase() {
        when(itemRepository.findAllByString(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto = itemService.findItem("iTEmNamE");
        assertEquals("itemName", itemDto.get(0).getName());
        assertEquals("itemDesc", itemDto.get(0).getDescription());
        assertEquals(true, itemDto.get(0).getAvailable());
    }

    @Test
    void findItemByDescriptionInNormalCase() {
        when(itemRepository.findAllByString(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto = itemService.findItem("itemDesc");
        assertEquals("itemName", itemDto.get(0).getName());
        assertEquals("itemDesc", itemDto.get(0).getDescription());
        assertEquals(true, itemDto.get(0).getAvailable());
    }

    @Test
    void findItemByDescriptionInRandomCase() {
        when(itemRepository.findAllByString(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto = itemService.findItem("ITemDESc");
        assertEquals("itemName", itemDto.get(0).getName());
        assertEquals("itemDesc", itemDto.get(0).getDescription());
        assertEquals(true, itemDto.get(0).getAvailable());
    }

    @Test
    void findItemWithoutCreatingItem() {
        List<ItemDto> itemDto = itemService.findItem("Item");
        assertEquals(Collections.emptyList(), itemDto);
    }

    @Test
    void addComment() {
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoToResponse commentDtoToResponse = itemService
                .addComment(2L, 1L, CommentMapper.toCommentDto(comment));
        assertEquals("commentText", commentDtoToResponse.getText());
        assertEquals("user name", commentDtoToResponse.getAuthorName());
    }

    @Test
    void addCommentWithUnknownUserShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment)));
        assertEquals("Пользователь с id " + user.getId() + " не обнаружен", exception.getMessage());
    }

    @Test
    void addCommentWithUnknownItemShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment)));
        assertEquals("Предмет с id " + item.getId() + " не обнаружен", exception.getMessage());
    }
}
