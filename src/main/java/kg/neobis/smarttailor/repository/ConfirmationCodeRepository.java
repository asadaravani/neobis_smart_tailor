package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    void deleteByExpirationTimeBefore(LocalDateTime date);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    Optional<ConfirmationCode> findByUserAndCode(AppUser user, Integer code);
}