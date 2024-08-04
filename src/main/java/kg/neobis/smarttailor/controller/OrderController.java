package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.CurrentOrganizationOrders;
import kg.neobis.smarttailor.dtos.OrganizationOrders;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.dtos.ads.list.OrderListDto;
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
            description = "The method accepts order data and images to create the order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestPart("order") String orderDto,
                                           @RequestPart("images") List<MultipartFile> images,
                                           Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(orderDto, images, authentication));
    }

    @Operation(
            summary = "DELETE ORDER",
            description = "To delete order, the method accepts its id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/delete-order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) throws IOException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderService.deleteOrder(orderId));
    }

    @Operation(
            summary = "GET ALL ORDERS",
            description = "The method returns list of orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Orders not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderListDto>> getAllOrders(@RequestParam int pageNumber,
                                                           @RequestParam int pageSize) {
        return ResponseEntity.ok(orderService.getAllOrders(pageNumber, pageSize));
    }

    @Operation(
            summary = "GET ORDER DETAILED",
            description = "The method accepts order's id, and then sends order's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get-order-detailed/{orderId}")
    public ResponseEntity<OrderDetailed> getOrderDetailed(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, authentication));
    }

    @Operation(
            summary = "HIDE ORDER",
            description = "The method accepts order's id and then makes order invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Order not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Order is already hidden | Only authors can hide their orders"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/hide/{orderId}")
    public ResponseEntity<String> hideOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.hideOrder(orderId, authentication));
    }

    @Operation(
            summary = "ASSIGN ORDER TO ORGANIZATION",
            description = "The method accepts order id and organization's name to assign order to specified organization",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order has been assigned to specified organization"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Order (or Organization) not found | Organization hasn't sent request | User is not a member of any organization"),
                    @ApiResponse(responseCode = "409", description = "User can't manage an order that is not his own"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/assign-order-to-organization/{orderId}")
    public ResponseEntity<String> assignOrderToOrganization(@PathVariable Long orderId, @RequestParam String organizationName, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.assignOrderToOrganization(orderId, organizationName, authentication));
    }

    @Operation(
            summary = "SEND REQUEST TO EXECUTE ORDER",
            description = "The method accepts order id and user's information from jwt to leave a request to execute order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User has left a request to execute the order"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Order not found | User has no permission to send request to execute order | User is not a member of any organization"),
                    @ApiResponse(responseCode = "409", description = "User can't respond to his/her own order | Order is already taken by another user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/send-request-to-execute-order/{orderId}")
    public ResponseEntity<String> sendRequestToExecuteOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.sendRequestToExecuteOrder(orderId, authentication));
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
    public ResponseEntity<List<OrganizationOrders>> getOrdersOfOrganization(Authentication authentication){
        return ResponseEntity.ok(orderService.getOrdersOfOrganization(authentication.getName()));
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
    @GetMapping("/organization-current-orders")
    public ResponseEntity<CurrentOrganizationOrders> getCurrentOrders(Authentication authentication){
        return ResponseEntity.ok(orderService.getCurrentOrdersOfOrganization(authentication.getName()));
    }

}