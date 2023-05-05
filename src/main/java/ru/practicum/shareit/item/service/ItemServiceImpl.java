package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не c id = " + userId + " не найден");
        });
        Item item = itemStorage.save(ItemMapper.createItem(itemDto, user));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    public List<ItemBookingDto> getAllUsersItems(long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не c id = " + userId + " не найден");
        });

        List<Item> itemList = itemStorage.findAllByUserIdOrderByIdAsc(userId);
        List<ItemBookingDto> itemBookingDtos = new ArrayList<>();
        for (Item item : itemList) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> bookingList = bookingStorage.findByItemAndStatusOrderByStart(item, Status.APPROVED);
            for (Booking booking : bookingList) {
                if ((booking.getEnd().isAfter(LocalDateTime.now()) &&
                        booking.getStart().isBefore(LocalDateTime.now())) ||
                        booking.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = booking;
                }
            }

            if (lastBooking != null) {
                bookingList.remove(lastBooking);
            }
            for (Booking booking : bookingList) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = booking;
                    break;
                }
            }

            BookingDto nextBookingDto = null;
            BookingDto lastBookingDto = null;
            if (nextBooking != null) {
                nextBookingDto = BookingMapper.toBookingDto(nextBooking);
            }
            if (lastBooking != null) {
                lastBookingDto = BookingMapper.toBookingDto(lastBooking);
            }
            itemBookingDtos.add(ItemMapper.toItemDtoBooking(item, nextBookingDto, lastBookingDto));
        }
        return itemBookingDtos;
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
        List<CommentDto> commentDtoList = commentList.stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
        if (item.getUser().getId() == userId) {

            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> bookingList = bookingStorage.findByItemAndStatusOrderByStart(item, Status.APPROVED);
            for (Booking booking : bookingList) {
                if ((booking.getEnd().isAfter(LocalDateTime.now()) &&
                        booking.getStart().isBefore(LocalDateTime.now())) ||
                        booking.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = booking;
                }
            }
                if (lastBooking != null) {
                    bookingList.remove(lastBooking);
                }
                for (Booking booking : bookingList) {
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        nextBooking = booking;
                        break;
                    }
                }
                BookingDto nextBookingDto = null;
                BookingDto lastBookingDto = null;
                if (nextBooking != null) {
                    nextBookingDto = BookingMapper.toBookingDto(nextBooking);
                }
                if (lastBooking != null) {
                    lastBookingDto = BookingMapper.toBookingDto(lastBooking);
                }
            return ItemMapper.toItemDtoBooking(item, nextBookingDto, lastBookingDto, commentDtoList);
        }
        return ItemMapper.toItemDtoBooking(item, commentDtoList);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto updatedItem) {
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Предмет с id " + itemId + " не обнаружен"));

        if (!(userId == item.getUser().getId())) {
            throw new ObjectNotFoundException("Предмет не принадлежит пользователю");
        }
        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
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
                    .map(ItemMapper::createItemDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        commentDto.setCreated(LocalDateTime.now());
        User user = userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id + " +
                userId + " не обнаружен"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Предмет с id " + itemId + " не обнаружен"));
        List<Booking> bookingsList = bookingStorage.findAllByBookerIdAndItemIdAndEndBefore(userId, itemId,
                LocalDateTime.now());
        for (Booking booking : bookingsList) {
            if (booking.getBooker().getId() == userId && booking.getItem().equals(item) &&
            booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = CommentMapper.toComment(user, item, commentDto);
                commentStorage.save(comment);
                return CommentMapper.toCommentDto(comment);
            }
        }
        throw new BookingException("Ошибка добавления комментария");
    }
}
