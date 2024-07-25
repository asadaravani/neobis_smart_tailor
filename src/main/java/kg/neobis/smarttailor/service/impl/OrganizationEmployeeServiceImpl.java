package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.repository.OrganizationEmployeeRepository;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationEmployeeServiceImpl implements OrganizationEmployeeService {

    OrganizationEmployeeRepository organizationEmployeeRepository;

    @Override
    public Boolean existsByEmployeeEmail(String email) {
        return organizationEmployeeRepository.existsByEmployeeEmail(email);
    }

    @Override
    public Boolean existsByPositionNameAndEmployeeEmail(String positionName, String employeeEmail) {
        return organizationEmployeeRepository.existsByPositionNameAndEmployeeEmail(positionName, employeeEmail);
    }

    @Override
    public void save(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.save(organizationEmployee);
    }

    @Override
    public OrganizationEmployee findOrganizationByEmployeeEmail(String email){
        return organizationEmployeeRepository.findByEmployeeEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("OrganizationEmployee Not Found"));
    }
}