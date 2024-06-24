package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {

    boolean existsByToken(String token);
}