package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Bloger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogerRepository extends JpaRepository<Bloger, Long> {
}
