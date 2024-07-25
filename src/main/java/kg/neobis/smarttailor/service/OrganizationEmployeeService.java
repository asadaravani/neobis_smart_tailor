package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.OrganizationEmployee;

public interface OrganizationEmployeeService {

    Boolean existsByEmployeeEmail(String email);

    Boolean existsByPositionNameAndEmployeeEmail(String positionName, String employeeEmail);

    void save(OrganizationEmployee organizationEmployee);

    OrganizationEmployee findOrganizationByEmployeeEmail(String email);
}