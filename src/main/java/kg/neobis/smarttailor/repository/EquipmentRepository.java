package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findAllByAuthor(AppUser user);

    Page<Equipment> findAllByAuthor(AppUser user, Pageable pageable);

    @Query("SELECT e FROM Equipment e JOIN e.equipmentBuyers b WHERE b = :user")
    List<Equipment> findUserEquipmentPurchases(@Param("user") AppUser user);

    Page<Equipment> findByIsVisibleAndQuantityGreaterThan(boolean isVisible, int quantity, Pageable pageable);

    Page<Equipment> findEquipmentByNameContainingIgnoreCase(String name, Pageable pageable);
}