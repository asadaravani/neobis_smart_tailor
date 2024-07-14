package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser findUserByEmail(String email);

    List<AppUser> findAllByEnabledFalseAndCreatedAtBefore(LocalDateTime cutoff);
}