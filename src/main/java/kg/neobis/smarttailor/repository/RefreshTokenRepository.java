package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByExpirationTimeBefore(LocalDateTime date);

    Boolean existsByToken(String token);
}