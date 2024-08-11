package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Page<Equipment> findAllByAuthor(AppUser user, Pageable pageable);

    Page<Equipment> findByIsVisibleAndQuantityGreaterThan(boolean isVisible, int quantity, Pageable pageable);

    Page<Equipment> findEquipmentByNameContainingIgnoreCase(String name, Pageable pageable);
}