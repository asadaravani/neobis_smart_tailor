package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.SubscriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionTokenRepository extends JpaRepository<SubscriptionToken, Long> {

    SubscriptionToken findByToken(String token);

    SubscriptionToken findByUser(AppUser user);
}