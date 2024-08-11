package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.EmployeeListDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.EmployeeService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {

    AppUserMapper appUserMapper;
    AppUserService appUserService;
    OrderService orderService;
    OrganizationEmployeeService organizationEmployeeService;

    @Override
    @Cacheable(value = "employees", key = "#authentication.name")
    public List<EmployeeListDto> getAllEmployees(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        Organization organization = organizationEmployee.getOrganization();
        List<OrganizationEmployee> organizationEmployees = organizationEmployeeService.findAllByOrganization(organization);

        return organizationEmployees.stream().map(orgEmp -> {
            List<Long> orderNumbers = orderService.getOrderIdsByEmployee(organizationEmployee.getEmployee());
            return appUserMapper.entityListToListDto(orgEmp, orderNumbers);
        }).collect(Collectors.toList());
    }
}
