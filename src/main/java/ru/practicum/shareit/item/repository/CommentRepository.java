package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select comment from Comment comment " +
            "where comment.item.id in :ids")
    List<Comment> findAllComments(@Param("ids") List<Long> ids);
}
