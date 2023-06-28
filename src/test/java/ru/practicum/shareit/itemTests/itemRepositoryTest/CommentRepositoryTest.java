package ru.practicum.shareit.itemTests.itemRepositoryTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;

    private Comment comment;

    @BeforeEach
    void createModel() {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.save(new User(1L, "User name", "user@mail.com"));
        User anotherUser = userRepository.save(new User(2L, "another name", "another@mail.com"));
        item = itemRepository.save(new Item(1L, "name", "desc", true, user, null));
        comment = commentRepository.save(new Comment(1L, "text", item, anotherUser, now));
    }

    @AfterEach
    void deleteAll() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllCommentsTest() {
        List<Long> ids = itemRepository.findAll().stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllComments(ids);

        assertEquals(List.of(comments).size(), comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
        assertEquals(comment.getAuthor(), comments.get(0).getAuthor());
        assertEquals(comment.getText(), comments.get(0).getText());
    }
}
