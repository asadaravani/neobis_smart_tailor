package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.ServiceDetailed;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Service")
@RequestMapping(EndpointConstants.SERVICE_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceController {

    ServicesService servicesService;

    @Operation(
            summary = "ADD SERVICE",
            description = "Accepts service data and images to create the service",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Service has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present | Validation failed"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/add-service")
    public ResponseEntity<String> addService(@RequestPart("service") String serviceDto,
                                             @RequestPart("images") List<MultipartFile> images,
                                             Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesService.addService(serviceDto, images, authentication));
    }

    @Operation(
            summary = "DELETE SERVICE",
            description = "Deletes service by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service has been deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type | Only authors can delete their advertisements"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @DeleteMapping("/delete-service/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable Long serviceId, Authentication authentication) throws IOException {
        return ResponseEntity.ok(servicesService.deleteService(serviceId, authentication));
    }

    @Operation(
            summary = "GET ALL SERVICES",
            description = "Returns list of services for marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-services")
    public ResponseEntity<AdvertisementPageDto> getAllServices(@RequestParam int pageNumber,
                                                               @RequestParam int pageSize) {
        return ResponseEntity.ok(servicesService.getAllServices(pageNumber, pageSize));
    }

    @Operation(
            summary = "GET SERVICE DETAILED",
            description = "Accepts service's id, and returns service's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-service-detailed/{serviceId}")
    public ServiceDetailed getServiceDetailed(@PathVariable Long serviceId) {
        return servicesService.getServiceById(serviceId);
    }

    @Operation(
            summary = "HIDE SERVICE",
            description = "Accepts service's id and then makes ad invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type | Only authors can hide their advertisements"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Service is already hidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/hide/{serviceId}")
    public ResponseEntity<String> hideService(@PathVariable Long serviceId, Authentication authentication) {
        return ResponseEntity.ok(servicesService.hideService(serviceId, authentication));
    }
}