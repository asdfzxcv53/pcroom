package com.example.pcroom.repositorytest;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.Role;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.DBRefreshTokenRepository;
import com.example.pcroom.infrastructure.RefreshTokenRepository;
import com.example.pcroom.presentation.controller.AuthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DBRefreshTokenRepository.class)
@ActiveProfiles("db")
public class DBRefreshTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("토큰 저장")
    public void tokenSaveTest() {

        //Given

        User user = new User("abc123", "1234", "승우", "01012341234", Role.USER);
        entityManager.persist(user);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), "tokenhash", Duration.ofDays(14));

        //When

        entityManager.persist(refreshToken);
        entityManager.flush();
        entityManager.clear();

        // then

        long start = System.currentTimeMillis();;
        RefreshToken found =
                refreshTokenRepository.findByUserId(user.getId()).orElseThrow();
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        assertThat(found.getHashedToken()).isEqualTo("tokenhash");
        assertThat(found.isExpired()).isFalse();
    }

    @Test
    @DisplayName("토큰 삭제")
    public void tokenDeleteTest() {

        //Given

        User user = new User("abc123", "1234", "승우", "01012341234", Role.USER);
        entityManager.persist(user);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), "tokenhash", Duration.ofDays(14));

        entityManager.persist(refreshToken);
        entityManager.flush();
        entityManager.clear();

        assertThat(refreshTokenRepository.findByUserId(user.getId())).isNotEmpty();

        //When

        refreshTokenRepository.deleteByUserId(user.getId());
        entityManager.clear();

        //Then

        assertThat(refreshTokenRepository.findByUserId(user.getId())).isEmpty();
    }
}
