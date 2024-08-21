package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EmployeeService {

    List<EmployeeListDto> getAllEmployees(Authentication authentication);

    List<EmployeeDto> getAvailableEmployees(Long orderId, Authentication authentication);

    EmployeeDetailedDto getEmployeeDetailed(Long employeeId, Authentication authentication);

    String removeEmployee(Long employeeId, Authentication authentication);

    AdvertisementPageDto searchAds(String query, int pageNumber, int pageSize, Authentication authentication);

    OrganizationEmployees getOrganizationEmployeesByWeight(Authentication authentication);

    EmployeesPageDto searchEmployees(String query, int pageNumber, int pageSize, Authentication authentication);
}