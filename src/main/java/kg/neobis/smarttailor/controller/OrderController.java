package kg.neobis.smarttailor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.enums.PlusMinus;
import kg.neobis.smarttailor.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@Tag(name = "Order")
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.ORDER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @Operation(
            summary = "ADD ORDER",
            description = "Accepts order's data and images to create the order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present | Entered data has not been validated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestPart("order") String order,
                                           @RequestPart("images") List<MultipartFile> images,
                                           Authentication authentication) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(order, images, authentication));
    }

    @Operation(
            summary = "ASSIGN EMPLOYEE TO ORDER",
            description = "The method accepts order id and organization's name to assign order to specified organization",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been assigned to specified employee"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Order (or Employee) not found | Employee is not a member of organization"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/assign-employee-to-order/{orderId}")
    public ResponseEntity<String> assignEmployeeToOrder(@PathVariable Long orderId, @RequestParam Long employeeId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.assignEmployeeToOrder(orderId, employeeId, authentication));
    }

    @Operation(
            summary = "ASSIGN USER TO ORDER",
            description = "Accepts order's and user's IDs to assign order to specified organization",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been assigned to specified organization"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order (or Organization) not found | Organization hasn't sent request | User is not a member of any organization"),
                    @ApiResponse(responseCode = "409", description = "User can't manage an order that is not his own | Order is already given to another organization"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/assign-executor-to-order/{orderId}")
    public ResponseEntity<String> assignExecutorToOrder(@PathVariable Long orderId,
                                                @RequestParam Long executorId,
                                                Authentication authentication) {
        return ResponseEntity.ok(orderService.assignExecutorToOrder(orderId, executorId, authentication));
    }

    @Operation(
            summary = "CHANGE ORDER STATUS",
            description = "Accepts order id, CUSTOM enum +/- ,  and user's information from jwt to leave a request to execute order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order's status is changed"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present | User has no permission to send request to execute order"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found | User is not a member of any organization"),
                    @ApiResponse(responseCode = "409", description = "User can't respond to his/her own order | Order is already taken by another user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/change-status/{orderId}/{plusMinus}")
    public ResponseEntity<String> changeOrderStatus(@PathVariable Long orderId,
                                                    @PathVariable PlusMinus plusMinus,
                                                    Authentication authentication) {
        return ResponseEntity.ok(orderService.changeOrderStatus(orderId, plusMinus, authentication));
    }

    @GetMapping("/organization-order-history")
    public ResponseEntity<AdvertisementPageDto> getOrganizationOrderHistory(@RequestParam int pageNumber,
                                                                            @RequestParam int pageSize,
                                                                            Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrganizationOrderHistory(pageNumber, pageSize, authentication));
    }

    @GetMapping("/organization-order-history/{orderId}")
    public ResponseEntity<OrganizationOrderHistoryDetailedDto> getOrganizationOrderHistoryDetailed(@PathVariable Long orderId,
                                                                                                   Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrganizationOrderHistoryDetailed(orderId, authentication));
    }
    @Operation(
            summary = "COMPLETE ORDER",
            description = "Accepts order's id and changes order status to completed",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been completed"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type | Order taken by another organization | User has no permission to complete order"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id | Customer hasn't chosen an executor to order"),
                    @ApiResponse(responseCode = "409", description = "Order is already completed"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/complete/{orderId}")
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.completeOrder(orderId, authentication));
    }

    @Operation(
            summary = "DELETE ORDER",
            description = "Deletes order by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Only authors can delete their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @DeleteMapping("/delete-order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId,
                                              Authentication authentication) throws IOException {
        return ResponseEntity.ok(orderService.deleteOrder(orderId, authentication));
    }

    @Operation(
            summary = "GET ALL ORDERS",
            description = "Returns list of visible orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-orders")
    public ResponseEntity<AdvertisementPageDto> getAllVisibleOrders(@RequestParam int pageNumber,
                                                                    @RequestParam int pageSize) {
        return ResponseEntity.ok(orderService.getAllVisibleOrders(pageNumber, pageSize));
    }

    @Operation(
            summary = "ORDERS OF THE ORGANIZATION BY STATUSES",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns a body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Organization Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/organization-current-orders-monitoring")
    public ResponseEntity<CurrentOrganizationOrders> getCurrentOrganizationOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getCurrentOrdersOfOrganization(authentication));
    }

    @Operation(
            summary = "CURRENT ORDER DETAILED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order detailed information has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Organization not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/current-order-detailed/{orderId}")
    public ResponseEntity<CurrentOrderDetailed> getCurrentOrderDetailed(@PathVariable Long orderId,
                                                                             Authentication authentication) {
        return ResponseEntity.ok(orderService.getCurrentOrderDetailed(orderId, authentication));
    }

    @Operation(
            summary = "GET EMPLOYEE'S ORDERS BY STAGE",
            description = "Returns list of employee's orders by stage (completed / current)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of employee's orders received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Employee not found with specified email | Invalid stage"),
                    @ApiResponse(responseCode = "409", description = "Authenticated user is not a member of any organization | Employee is not a member of authenticated user organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-employee-orders-by-stage/{employeeId}")
    public ResponseEntity<EmployeePageDto> getEmployeeOrderByStage(@PathVariable Long employeeId,
                                                                   @RequestParam String stage,
                                                                   @RequestParam int pageNumber,
                                                                   @RequestParam int pageSize,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(orderService.getEmployeeOrdersByStage(employeeId, stage, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "GET ORDER DETAILED",
            description = "Accepts order's id, and returns order's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order's information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-order-detailed/{orderId}")
    public ResponseEntity<OrderDetailed> getOrderDetailed(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailed(orderId));
    }

    @Operation(
            summary = "GET ORDER DETAILED FOR AUTHOR",
            description = "Accepts order's id, and returns order's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "User is not an author of this order"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get-order-detailed-for-author/{orderId}")
    public ResponseEntity<AuthorOrderDetailedDto> getOrderDetailedForAuthor(@PathVariable Long orderId,
                                                                            Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderDetailedForAuthor(orderId, authentication));
    }

    @Operation(
            summary = "GET ORDERS OF ORGANIZATION",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns a list of orders"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Organization Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/organization-orders")
    public ResponseEntity<List<OrganizationOrdersDto>> getOrdersOfOrganization(Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrdersOfOrganization(authentication.getName()));
    }

    @Operation(
            summary = "GET ORGANIZATION'S ORDERS BY STAGE",
            description = "Returns list of organization's orders by stage (completed / current)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of organization's orders received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Invalid stage"),
                    @ApiResponse(responseCode = "409", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-organization-orders-by-stage")
    public ResponseEntity<OrganizationPageDto> getOrganizationOrderByStage(@RequestParam int pageNumber,
                                                                           @RequestParam int pageSize,
                                                                           @RequestParam String stage,
                                                                           Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrganizationOrdersByStage(stage, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "GET USER'S ORDER HISTORY BY STAGE",
            description = "Returns list of user's order history by stage (completed / current)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of user's orders received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Invalid stage"),
                    @ApiResponse(responseCode = "409", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/order-history-for-user")
    public ResponseEntity<AdvertisementPageDto> getUserOrderHistoryByStage(@RequestParam int pageNumber,
                                                                           @RequestParam int pageSize,
                                                                           @RequestParam String stage,
                                                                           Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrderHistoryByStage(stage, pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "USER'S ORDERS",
            description = "Returns list of user's orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/my-orders")
    public ResponseEntity<AdvertisementPageDto> getUserOrders(@RequestParam int pageNumber,
                                                              @RequestParam int pageSize,
                                                              Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrders(pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "HIDE ORDER",
            description = "Accepts order's id and then makes ad invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Order is already hidden | Only authors can hide their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/hide/{orderId}")
    public ResponseEntity<String> hideOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.hideOrder(orderId, authentication));
    }

    @Operation(
            summary = "SEND REQUEST TO EXECUTE ORDER",
            description = "Accepts order id and user's information from jwt to leave a request to execute order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User has left a request to execute the order"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present | User has no permission to send request to execute order"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found | User is not a member of any organization"),
                    @ApiResponse(responseCode = "409", description = "User can't respond to his/her own order | Order is already taken by another user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/send-request-to-execute-order/{orderId}")
    public ResponseEntity<String> sendRequestToExecuteOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.sendRequestToExecuteOrder(orderId, authentication));
    }

    @GetMapping("/organization-order-history-by-employee/{employeeId}")
    public ResponseEntity<AdvertisementPageDto> getOrganizationOrderHistoryByUser(@PathVariable Long employeeId,
                                                                                  @RequestParam String stage,
                                                                                  @RequestParam int pageNumber,
                                                                                  @RequestParam int pageSize,
                                                                                  Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrganizationOrderHistoryByEmployee(employeeId, stage, pageNumber, pageSize, authentication));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, authentication));
    }
}