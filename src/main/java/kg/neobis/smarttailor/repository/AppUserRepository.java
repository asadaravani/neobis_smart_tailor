package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);
 }