package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.EmployeeDetailedDto;
import kg.neobis.smarttailor.dtos.EmployeeDto;
import kg.neobis.smarttailor.dtos.EmployeeListDto;
import kg.neobis.smarttailor.dtos.EmployeesPageDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EmployeeService {

    List<EmployeeListDto> getAllEmployees(Authentication authentication);

    List<EmployeeDto> getAvailableEmployees(Long orderId, Authentication authentication);

    EmployeeDetailedDto getEmployeeDetailed(Long employeeId, Authentication authentication);

    EmployeesPageDto searchEmployees(String query, int pageNumber, int pageSize, Authentication authentication);

    AdvertisementPageDto searchAds(String query, int pageNumber, int pageSize, Authentication authentication);
}