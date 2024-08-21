package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.service.EmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Employee")
@RequestMapping(EndpointConstants.EMPLOYEE_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {

    EmployeeService employeeService;

    @Operation(
            summary = "GET ALL EMPLOYEES",
            description = "Returns list of organization's employees",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-employees")
    public ResponseEntity<List<EmployeeListDto>> getAllEmployees(Authentication authentication) {
        return ResponseEntity.ok(employeeService.getAllEmployees(authentication));
    }

    @Operation(
            summary = "EMPLOYEE'S INFORMATION",
            description = "Returns employee's information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee's information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "409", description = "User is not an employee | User is not a member of any organization | User is not a member of authenticated user organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/profile/{employeeId}")
    public ResponseEntity<EmployeeDetailedDto> getUserProfile(@PathVariable Long employeeId, Authentication authentication) {
        return ResponseEntity.ok(employeeService.getEmployeeDetailed(employeeId, authentication));
    }

    @GetMapping("/available-employees/{orderId}")
    public ResponseEntity<List<EmployeeDto>> getAvailableEmployees(@PathVariable Long orderId,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(employeeService.getAvailableEmployees(orderId, authentication));
    }

    @DeleteMapping("/remove/{employeeId}")
    public ResponseEntity<String> removeEmployee(@PathVariable Long employeeId,
                                                 Authentication authentication) {
        return ResponseEntity.ok(employeeService.removeEmployee(employeeId, authentication));
    }
}