package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByEnabledFalseAndCreatedAtBefore(LocalDateTime cutoff);
}