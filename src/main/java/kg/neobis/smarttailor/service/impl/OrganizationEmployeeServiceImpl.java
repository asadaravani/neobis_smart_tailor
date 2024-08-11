package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.repository.OrganizationEmployeeRepository;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationEmployeeServiceImpl implements OrganizationEmployeeService {

    OrganizationEmployeeRepository organizationEmployeeRepository;

    @Override
    @Cacheable(value = "employeeAccessRights", key = "#accessRight + '_' + #employeeEmail")
    public Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail) {
        return organizationEmployeeRepository.existsByPosition_AccessRightsIsContainingAndEmployeeEmail(accessRight, employeeEmail);
    }

    @Override
    @Cacheable(value = "employeeEmails", key = "#email")
    public Boolean existsByEmployeeEmail(String email) {
        return organizationEmployeeRepository.existsByEmployeeEmail(email);
    }

    @Override
    @Cacheable(value = "orgEmployeeEmails", key = "#organization.id + '_' + #employeeEmail")
    public Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail) {
        return organizationEmployeeRepository.existsByOrganizationAndEmployeeEmail(organization, employeeEmail);
    }

    @Override
    @Cacheable(value = "organizationEmployees", key = "#organization.id")
    public List<OrganizationEmployee> findAllByOrganization(Organization organization) {
        return organizationEmployeeRepository.findAllByOrganization(organization);
    }

    @Override
    @Cacheable(value = "employeeByEmail", key = "#email")
    public Optional<OrganizationEmployee> findByEmployeeEmail(String email){
        return organizationEmployeeRepository.findByEmployeeEmail(email);
    }

    @Override
    public void save(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.save(organizationEmployee);
    }
}