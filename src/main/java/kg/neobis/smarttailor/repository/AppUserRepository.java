package kg.neobis.smarttailor.repository;

import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM AppUser u WHERE u.createdAt < :cutoffDate AND u.enabled = FALSE")
    void deleteNotEnabledUsers(LocalDateTime cutoffDate);

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);
}