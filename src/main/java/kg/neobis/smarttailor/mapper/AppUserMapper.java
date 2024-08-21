package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppUserMapper {

    private final OrganizationEmployeeService organizationEmployeeService;

    public EmployeeListDto entityListToEmployeeListDto(OrganizationEmployee organizationEmployee, List<EmployeeOrderListDto> orderInfo) {
        AppUser employee = organizationEmployee.getEmployee();
        return new EmployeeListDto(
                employee.getId(),
                String.format("%s %s %s", employee.getSurname(), employee.getName(), employee.getPatronymic()),
                employee.getEmail(),
                orderInfo,
                organizationEmployee.getPosition().getName()
        );
    }

    public EmployeeDetailedDto entityToEmployeeDetailedDto(AppUser user, String positionName) {
        return new EmployeeDetailedDto(
                user.getId(),
                user.getImage().getUrl(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getEmail(),
                user.getPhoneNumber(),
                positionName
        );
    }

    public EmployeeCard toEmployeeCard(OrganizationEmployee employee) {

        Position position = employee.getPosition();
        AppUser user = employee.getEmployee();

        return new EmployeeCard(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                position.getName(),
                position.getWeight(),
                position.getAccessRights()
        );
    }

    public EmployeeDto entityToEmployeeDto(AppUser appUser) {
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(appUser.getEmail());

        return new EmployeeDto(
                appUser.getId(),
                String.format("%s %s %s", appUser.getSurname(), appUser.getName(), appUser.getPatronymic()),
                appUser.getImage() != null ? appUser.getImage().getUrl() : null,
                organizationEmployee.getPosition().getName()
        );
    }

    public UserProfileDto entityToUserProfileDto(AppUser user, boolean inOrganization) {
        return new UserProfileDto(
                user.getId(),
                user.getImage().getUrl(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getHasSubscription(),
                inOrganization
        );
    }
}