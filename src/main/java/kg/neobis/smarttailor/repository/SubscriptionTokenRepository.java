package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.SubscriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SubscriptionTokenRepository extends JpaRepository<SubscriptionToken, Long> {

    void deleteByExpirationTimeBefore(LocalDateTime date);

    SubscriptionToken findByToken(String token);
}