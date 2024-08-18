package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.repository.OrganizationEmployeeRepository;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationEmployeeServiceImpl implements OrganizationEmployeeService {

    OrganizationEmployeeRepository organizationEmployeeRepository;

    @Override
    public Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail) {
        return organizationEmployeeRepository.existsByPosition_AccessRightsIsContainingAndEmployeeEmail(accessRight, employeeEmail);
    }

    @Override
    public Boolean existsByEmployeeEmail(String email) {
        return organizationEmployeeRepository.existsByEmployeeEmail(email);
    }

    @Override
    public Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail) {
        return organizationEmployeeRepository.existsByOrganizationAndEmployeeEmail(organization, employeeEmail);
    }

    @Override
    public List<OrganizationEmployee> findAllByOrganization(Organization organization) {
        return organizationEmployeeRepository.findAllByOrganization(organization);
    }

    @Override
    public OrganizationEmployee findByEmployeeEmail(String email){
        return organizationEmployeeRepository.findByEmployeeEmail(email)
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
    }

    @Override
    public List<AppUser> findEmployeesWithPositionWeightLessThan(int weight, Long organizationId, Long orderId) {
        return organizationEmployeeRepository.findUnassignedEmployeesWithPositionWeightLessThanInOrganization(organizationId, weight, orderId);
    }

    @Override
    public List<AppUser> findEmployeesByOrganization(Organization organization) {
        return organizationEmployeeRepository.findEmployeesByOrganization(organization);
    }

    @Override
    public void save(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.save(organizationEmployee);
    }
}