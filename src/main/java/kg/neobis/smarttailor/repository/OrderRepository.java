package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByAuthor(AppUser user);
}