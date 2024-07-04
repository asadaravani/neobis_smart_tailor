package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.OrderDetailsDto;
import kg.neobis.smarttailor.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@Tag(name = "Order")
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.ORDER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService service;

    @Operation(
            summary = "creating order",
            description = "The authorized user transmits order's information and photos to create the order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "order has been created"),
                    @ApiResponse(responseCode = "403", description = "authentication required"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestPart("order") String equipmentDto,
                                          @RequestPart("images") List<MultipartFile> images,
                                          Authentication authentication) {
        return ResponseEntity.ok(service.addOrder(equipmentDto, images, authentication));
    }

    @Operation(
            summary = "get order's detailed information",
            description = "The authorized user transmits the order id to receive detailed information about the order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order information was displayed"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "403", description = "authentication required"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @GetMapping("/get-by-id/{orderId}")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getOrderById(orderId));
    }
}