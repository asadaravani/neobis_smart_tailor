package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {

    List<Services> findAllByAuthor(AppUser user);

    Page<Services> findAllByAuthor(AppUser user, Pageable pageable);

    @Query("SELECT s FROM service s JOIN s.serviceApplicants a WHERE a = :user")
    List<Services> findUserServicePurchases(@Param("user") AppUser user);

    Page<Services> findByIsVisible(boolean isVisible, Pageable pageable);

    Page<Services> findServicesByNameContainingIgnoreCaseAndIsVisibleTrue(String name, Pageable pageable);
}