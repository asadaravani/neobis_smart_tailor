package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationEmployeeRepository extends JpaRepository<OrganizationEmployee, Long> {

    Boolean existsByEmployeeEmail(String email);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);

    Boolean existsByPosition_AccessRightsIsContainingAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    Optional<OrganizationEmployee> findByEmployeeEmail(String email);
}