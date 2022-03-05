package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @EntityGraph(attributePaths = {"bloger", "category"})
    Page<Blog> findAllByCategory(Category category, Pageable pageable);
}
