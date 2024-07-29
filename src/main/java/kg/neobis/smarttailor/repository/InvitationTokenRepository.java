package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.InvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationTokenRepository extends JpaRepository<InvitationToken, Long> {

    InvitationToken findByToken(String token);

    InvitationToken findByUser(AppUser user);
}