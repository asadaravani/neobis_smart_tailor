package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;

import java.util.List;

public interface OrganizationEmployeeService {

    Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    Boolean existsByEmployeeEmail(String email);

    void save(OrganizationEmployee organizationEmployee);

    List<OrganizationEmployee> findAllByOrganization(Organization organization);

    OrganizationEmployee findByEmployeeEmail(String email);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);
}