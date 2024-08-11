package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByAuthor(AppUser user, Pageable pageable);

    List<Order> findAllByOrganizationExecutor(Organization organization);

    Page<Order> findByIsVisible(boolean isVisible, Pageable pageable);

    @Query("SELECT o FROM orders o JOIN o.orderEmployees e WHERE e = :user")
    List<Order> findByOrderEmployee(@Param("user") AppUser user);

    @Query("SELECT o FROM orders o JOIN o.orderEmployees e WHERE e = :user AND o.dateOfCompletion IS NULL")
    Page<Order> findCurrentEmployeeOrders(@Param("user") AppUser user, Pageable pageable);

    @Query("SELECT o FROM orders o WHERE o.organizationExecutor = :organization AND o.dateOfCompletion IS NULL")
    Page<Order> findCurrentOrganizationOrders(@Param("organization") Organization organization, Pageable pageable);

    @Query("SELECT o FROM orders o JOIN o.orderEmployees e WHERE e = :user AND o.dateOfCompletion IS NOT NULL")
    Page<Order> findCompletedEmployeeOrders(@Param("user") AppUser user, Pageable pageable);

    @Query("SELECT o FROM orders o WHERE o.organizationExecutor = :organization AND o.dateOfCompletion IS NOT NULL")
    Page<Order> findCompletedOrganizationOrders(@Param("organization") Organization organization, Pageable pageable);
}