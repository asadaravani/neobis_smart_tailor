package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementCard;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.EmployeesPageDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrganizationEmployeeService {

    Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    Boolean existsByEmployeeEmail(String email);

    void save(OrganizationEmployee organizationEmployee);

    List<AppUser> findEmployeesWithPositionWeightLessThan(int weight, Long organizationId, Long orderId);

    Page<AdvertisementCard> searchAcrossTable(String query, Long userId, Long organizationId, Pageable pageable);

    List<OrganizationEmployee> findAllByOrganization(Organization organization);

    OrganizationEmployee findByEmployeeEmail(String email);

    List<AppUser> findEmployeesByOrganization(Organization organization);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);

    Page<OrganizationEmployee> findEmployee(String query, String query1, String query2, Organization organization, Pageable pageable);

}