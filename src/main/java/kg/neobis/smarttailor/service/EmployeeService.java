package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.EmployeeListDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EmployeeService {

    List<EmployeeListDto> getAllEmployees(Authentication authentication);
}
