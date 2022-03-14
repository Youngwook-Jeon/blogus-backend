package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"bloger"})
    List<Comment> findAllByBlogIdOrderByCreatedAt(Long blogId);

    @Override
    @EntityGraph(attributePaths = {"bloger"})
    Optional<Comment> findById(Long id);
}
