package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
}