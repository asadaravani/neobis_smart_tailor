package kg.neobis.smarttailor.repository;

import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    Optional<ConfirmationCode> findByUser(AppUser user);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    Optional<ConfirmationCode> findByUserAndCode(AppUser user, Integer code);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConfirmationCode c WHERE c.user = :user")
    void deleteByUser(@Param("user") AppUser user);
}