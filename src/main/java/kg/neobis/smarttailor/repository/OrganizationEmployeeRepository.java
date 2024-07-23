package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.OrganizationEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationEmployeeRepository extends JpaRepository<OrganizationEmployee, Long> {

    Boolean existsByEmployeeEmail(String email);

    Boolean existsByPositionNameAndEmployeeEmail(String positionName, String employeeEmail);
}