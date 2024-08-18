package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;

import java.util.List;

public interface OrganizationEmployeeService {

    Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    Boolean existsByEmployeeEmail(String email);

    void save(OrganizationEmployee organizationEmployee);

    List<AppUser> findEmployeesWithPositionWeightLessThan(int weight, Long organizationId, Long orderId);

    List<OrganizationEmployee> findAllByOrganization(Organization organization);

    OrganizationEmployee findByEmployeeEmail(String email);

    List<AppUser> findEmployeesByOrganization(Organization organization);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);
}