package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationEmployeeRepository extends JpaRepository<OrganizationEmployee, Long> {

    Boolean existsByEmployeeEmail(String email);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);

    Boolean existsByPosition_AccessRightsIsContainingAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    @Query("SELECT oe.employee FROM OrganizationEmployee oe WHERE oe.organization = :organization")
    List<AppUser> findEmployeesByOrganization(@Param("organization") Organization organization);

    List<OrganizationEmployee> findAllByOrganization(Organization organization);

    Optional<OrganizationEmployee> findByEmployeeEmail(String email);

    @Query("SELECT oe.employee FROM OrganizationEmployee oe " +
            "WHERE oe.position.weight < :weight " +
            "AND oe.organization.id = :organizationId " +
            "AND oe.employee NOT IN (SELECT e FROM orders o JOIN o.orderEmployees e WHERE o.id = :orderId)")
    List<AppUser> findUnassignedEmployeesWithPositionWeightLessThanInOrganization(
            @Param("organizationId") Long organizationId,
            @Param("weight") int weight,
            @Param("orderId") Long orderId);
}