package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.repository.OrganizationEmployeeRepository;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<OrganizationEmployee> findByEmployeeEmail(String email){
        return organizationEmployeeRepository.findByEmployeeEmail(email);
    }

    @Override
    public void save(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.save(organizationEmployee);
    }
}