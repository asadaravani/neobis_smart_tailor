package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.EmployeeDetailedDto;
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
    public List<EmployeeListDto> getAllEmployees(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        Organization organization = organizationEmployee.getOrganization();
        List<OrganizationEmployee> organizationEmployees = organizationEmployeeService.findAllByOrganization(organization);

        return organizationEmployees.stream().map(orgEmp -> {
            List<String> orderNames = orderService.getOrderNamesByEmployee(organizationEmployee.getEmployee());
            return appUserMapper.entityListToListDto(orgEmp, orderNames);
        }).collect(Collectors.toList());
    }

    @Override
    public EmployeeDetailedDto getEmployeeDetailed(Long employeeId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        AppUser employee = appUserService.findUserById(employeeId);
        OrganizationEmployee employeeData = organizationEmployeeService.findByEmployeeEmail(employee.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not an employee"));
        Organization employeeOrganization = employeeData.getOrganization();

        Boolean isAuthenticatedUserInOrganization = organizationEmployeeService.existsByOrganizationAndEmployeeEmail(employeeOrganization, user.getEmail());

        if (!isAuthenticatedUserInOrganization) {
            throw new UserNotInOrganizationException("Authenticated user is not a member of employee's organization");
        }

        return appUserMapper.entityToEmployeeDetailedDto(user, employeeData.getPosition().getName());
    }
}
