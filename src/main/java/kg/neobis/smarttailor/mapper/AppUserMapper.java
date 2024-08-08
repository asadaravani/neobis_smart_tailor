package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.EmployeeListDto;
import kg.neobis.smarttailor.dtos.UserProfileDto;
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

    public EmployeeListDto entityListToListDto(OrganizationEmployee organizationEmployee, List<Long> orderNumbers) {
        AppUser employee = organizationEmployee.getEmployee();
        return new EmployeeListDto(
                employee.getId(),
                String.format("%s %s %s", employee.getSurname(), employee.getName(), employee.getPatronymic()),
                employee.getEmail(),
                orderNumbers,
                organizationEmployee.getPosition().getName()
        );
    }
}