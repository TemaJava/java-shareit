package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ShortBookingDtoToResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;
    private final RequestRepository requestStorage;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь с id = " + userId + " не найден");
        });
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestStorage.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new ObjectNotFoundException("Запрос не найден"));
        }
        Item item = itemStorage.save(ItemMapper.createItem(itemDto, user, request));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    public List<ItemBookingDto> getAllUsersItems(long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь c id = " + userId + " не найден");
        });
        List<Item> itemList = itemStorage.findAllByUserIdOrderByIdAsc(userId);
        return setBookings(itemList);
    }

    @Override
    public ItemBookingDto getItemById(long userId, long itemId) {

        Item item = itemStorage.findById(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Предмет с id " + itemId + " не обнаружен");
        });
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь c id = " + userId + " не найден");
        });

        List<Comment> commentList = commentStorage.findAllComments(List.of(item.getId()));
        List<CommentDtoToResponse> commentDtoToResponseList = commentList.stream()
                .map(CommentMapper::toCommentDtoToResponse).collect(toList());
        if (item.getUser().getId() == userId) {
            List<Item> items = new ArrayList<>();
            items.add(item);
            List<ItemBookingDto> itemBookingDtoList = setBookings(items);
            return ItemMapper.toItemDtoBooking(itemBookingDtoList.get(0), commentDtoToResponseList);
        }
        return ItemMapper.toItemDtoBooking(item, commentDtoToResponseList);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto updatedItem) {
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Предмет с id " + itemId + " не обнаружен"));

        if (!(userId == item.getUser().getId())) {
            throw new ObjectNotFoundException("Предмет не принадлежит пользователю");
        }
        if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null && !updatedItem.getDescription().isBlank()) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }
        itemStorage.save(item);
        return ItemMapper.createItemDto(item);
    }

    @Override
    public List<ItemDto> findItem(String string) {
        if (string.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemStorage.findAllByString(string).stream()
                    .map(ItemMapper::createItemDto).collect(toList());
        }
    }

    @Override
    @Transactional
    public CommentDtoToResponse addComment(long userId, long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        User user = userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id " +
                userId + " не обнаружен"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Предмет с id " + itemId + " не обнаружен"));
        List<Booking> bookingsList = bookingStorage.findAllByBookerIdAndItemIdAndEndBefore(userId, itemId,
                LocalDateTime.now());
        for (Booking booking : bookingsList) {
            if (booking.getBooker().getId() == userId && booking.getItem().equals(item) &&
                    booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = CommentMapper.toComment(user, item, commentDto, now);
                commentStorage.save(comment);
                return CommentMapper.toCommentDtoToResponse(comment);
            }
        }
        throw new BookingException("Ошибка добавления комментария");
    }

    private List<ItemBookingDto> setBookings(List<Item> itemList) {
        List<ItemBookingDto> itemBookingDtos = new ArrayList<>();
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(toList());
        Map<Item, List<Booking>> bookingsItem = bookingStorage.getAllBookingByItem(idList,
                        Status.APPROVED, Sort.by(Sort.Direction.DESC, "start")).stream()
                .collect(groupingBy(Booking::getItem, toList()));
        LocalDateTime now = LocalDateTime.now();
        for (Item item : itemList) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            if (bookingsItem.get(item) != null) {
                List<Booking> bookingList = bookingsItem.get(item);
                nextBooking = bookingList.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .reduce((first, second) -> second).orElse(null);
                lastBooking = bookingList.stream()
                        .filter(b -> b.getStart().isBefore(now))
                        .findFirst().orElse(null);
            }
            ShortBookingDtoToResponse nextBookingDto = null;
            ShortBookingDtoToResponse lastBookingDto = null;
            if (nextBooking != null) {
                nextBookingDto = BookingMapper.toShortBookingDtoToResponse(nextBooking);
            }
            if (lastBooking != null) {
                lastBookingDto = BookingMapper.toShortBookingDtoToResponse(lastBooking);
            }
            itemBookingDtos.add(ItemMapper.toItemDtoBooking(item, nextBookingDto, lastBookingDto));
        }
        return itemBookingDtos;
    }
}
