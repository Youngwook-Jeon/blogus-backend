package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Bloger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogerRepository extends JpaRepository<Bloger, Long> {
    Optional<Bloger> findByName(String username);

    Optional<Bloger> findByEmail(String email);
}
