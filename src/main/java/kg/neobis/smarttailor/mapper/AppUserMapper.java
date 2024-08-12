package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppUserMapper {

    public UserProfileDto entityToDto(AppUser user, boolean inOrganization) {
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

    public static EmployeeDto appUserToEmployeeDto(AppUser appUser) {
        return new EmployeeDto(
                String.format("%s %s %s", appUser.getSurname(), appUser.getName(), appUser.getPatronymic()),
                appUser.getImage() != null ? appUser.getImage().getUrl() : null
        );
    }

    public EmployeeListDto entityListToListDto(OrganizationEmployee organizationEmployee, List<String> orderNames) {
        AppUser employee = organizationEmployee.getEmployee();
        return new EmployeeListDto(
                employee.getId(),
                String.format("%s %s %s", employee.getSurname(), employee.getName(), employee.getPatronymic()),
                employee.getEmail(),
                orderNames,
                organizationEmployee.getPosition().getName()
        );
    }
}