package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class BlogerRepositoryTests {

    @Autowired
    private BlogerRepository blogerRepository;

    private Bloger bloger;

    @BeforeEach
    public void setUp() {
        bloger = Bloger.builder()
                .name("Mayer")
                .email("mayerjeon@gmail.com")
                .password("P4ssword!@#$")
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("test for saving a user")
    public void givenBloger_whenSaved_thenReturnSavedBloger() {
        // given
        // when
        Bloger savedBloger = blogerRepository.save(bloger);

        // then
        assertThat(savedBloger).isNotNull();
        assertThat(savedBloger.getId()).isGreaterThan(0L);
    }
}
