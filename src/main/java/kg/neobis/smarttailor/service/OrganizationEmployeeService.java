package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementCard;
import kg.neobis.smarttailor.dtos.MyAdvertisementCard;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationEmployeeService {

    void delete(OrganizationEmployee organizationEmployee);

    Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail);

    Boolean existsByEmployeeEmail(String email);

    Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail);

    List<OrganizationEmployee> findAllByOrganization(Organization organization);

    OrganizationEmployee findByEmployeeEmail(String email);

    Page<OrganizationEmployee> findEmployee(String query, String query1, String query2, Organization organization, Pageable pageable);

    List<AppUser> findEmployeesByOrganization(Organization organization);

    List<AppUser> findEmployeesWithPositionWeightLessThan(int weight, Long organizationId, Long orderId);

    void save(OrganizationEmployee organizationEmployee);

    Page<MyAdvertisementCard> searchAcrossTable(String query, Long userId, Pageable pageable);
}