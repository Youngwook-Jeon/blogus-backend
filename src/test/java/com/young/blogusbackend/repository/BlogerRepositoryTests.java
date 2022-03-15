package com.young.blogusbackend.repository;

import com.young.blogusbackend.model.Bloger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.young.blogusbackend.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class BlogerRepositoryTests {

    @Autowired
    private BlogerRepository blogerRepository;

    private Bloger bloger;

    @BeforeEach
    public void setUp() {
        bloger = createValidUser();
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

    @Test
    @DisplayName("test for findById method")
    public void givenSavedUser_whenFindById_thenReturnTheUser() {
        // given
        Bloger savedBloger = blogerRepository.save(bloger);

        // when
        Optional<Bloger> bloger = blogerRepository.findById(savedBloger.getId());

        // then
        assertThat(bloger.isPresent()).isTrue();
        assertThat(bloger.get()).isEqualTo(savedBloger);
    }

    @Test
    @DisplayName("test for findByName method")
    public void givenSavedUser_whenFindByName_thenReturnTheUser() {
        // given
        Bloger savedBloger = blogerRepository.save(bloger);

        // when
        Optional<Bloger> bloger = blogerRepository.findByName(VALID_USER_NAME);

        // then
        assertThat(bloger.isPresent()).isTrue();
        assertThat(bloger.get()).isEqualTo(savedBloger);
    }

    @Test
    @DisplayName("test for findByEmail method")
    public void givenSavedUser_whenFindByEmail_thenReturnTheUser() {
        // given
        Bloger savedBloger = blogerRepository.save(bloger);

        // when
        Optional<Bloger> bloger = blogerRepository.findByEmail(VALID_USER_EMAIL);

        // then
        assertThat(bloger.isPresent()).isTrue();
        assertThat(bloger.get()).isEqualTo(savedBloger);
    }
}
