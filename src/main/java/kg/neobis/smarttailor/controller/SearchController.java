package kg.neobis.smarttailor.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.EmployeesPageDto;
import kg.neobis.smarttailor.dtos.OrderPageDto;
import kg.neobis.smarttailor.service.EmployeeService;
import kg.neobis.smarttailor.service.EquipmentService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Search")
@RequestMapping(EndpointConstants.SEARCH_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchController {

    EquipmentService equipmentService;
    ServicesService servicesService;
    EmployeeService employeeService;
    OrderService orderService;


    @Operation(
            summary = "SEARCH EQUIPMENTS IN THE MARKETPLACE",
            description = "Accepts equipment name and returns list of equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found"),
            }
    )
    @GetMapping("/equipment")
    public ResponseEntity<AdvertisementPageDto> searchEquipments(@RequestParam(name = "query") String query,
                                                                 @RequestParam int pageNumber,
                                                                 @RequestParam int pageSize,
                                                                 Authentication authentication) {
        return ResponseEntity.ok().body(equipmentService.searchEquipments(query, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "SEARCH SERVICES IN THE MARKETPLACE",
            description = "Accepts service name and returns list of services",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found"),
            }
    )
    @GetMapping("/service")
    public ResponseEntity<AdvertisementPageDto> searchServices(@RequestParam(name = "query") String query,
                                                                 @RequestParam int pageNumber,
                                                                 @RequestParam int pageSize,
                                                               Authentication authentication) {
        return ResponseEntity.ok().body(servicesService.searchServices(query, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "SEARCH EMPLOYEES",
            description = "Accepts employee name or surname and returns list of employees which are contained in the query",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employee list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Employee not found"),
            }
    )
    @GetMapping("/employee")
    public ResponseEntity<EmployeesPageDto> searchEmployees(@RequestParam(name = "query") String query,
                                                            @RequestParam int pageNumber,
                                                            @RequestParam int pageSize,
                                                            Authentication authentication) {
        return ResponseEntity.ok().body(employeeService.searchEmployees(query, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "SEARCH ORDERS",
            description = "Accepts order name and returns list of order which are contained in the query",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
            }
    )
    @GetMapping("/order")
    public ResponseEntity<OrderPageDto> searchOrders(@RequestParam(name = "query") String query,
                                                     @RequestParam int pageNumber,
                                                     @RequestParam int pageSize,
                                                     Authentication authentication) {
        return ResponseEntity.ok().body(orderService.searchOrders(query, pageNumber, pageSize, authentication));
    }



    @Operation(
            summary = "SEARCH MY ADVERTISEMENT",
            description = "Accepts order name and returns list of advertisements (equipment, order, service) which are contained in the query",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Advertisement list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Advertisement not found"),
            }
    )
    @GetMapping("/my-advertisements")
    public ResponseEntity<AdvertisementPageDto> searchAdvertisements(@RequestParam(name = "query") String query,
                                                     @RequestParam int pageNumber,
                                                     @RequestParam int pageSize,
                                                     Authentication authentication) {


        return ResponseEntity.ok().body(employeeService.searchAds(query, pageNumber, pageSize, authentication));
    }













}
