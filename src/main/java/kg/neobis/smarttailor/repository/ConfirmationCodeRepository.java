package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    ConfirmationCode findByUser(AppUser user);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    Optional<ConfirmationCode> findByUserAndCode(AppUser user, Integer code);
}