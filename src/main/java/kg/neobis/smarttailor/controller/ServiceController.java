package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.AuthorServiceDetailedDto;
import kg.neobis.smarttailor.dtos.ServiceDetailed;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "Service")
@RequestMapping(EndpointConstants.SERVICE_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceController {

    ServicesService servicesService;

    @Operation(
            summary = "ADD SERVICE",
            description = "Accepts service's data and images to create the service",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Service has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present |  Entered data has not been validated"),
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
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Only authors can delete their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @DeleteMapping("/delete-service/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable Long serviceId,
                                                Authentication authentication) throws IOException {
        return ResponseEntity.ok(servicesService.deleteService(serviceId, authentication));
    }

    @Operation(
            summary = "GET ALL SERVICES",
            description = "Returns list of visible services",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-services")
    public ResponseEntity<AdvertisementPageDto> getAllVisibleServices(@RequestParam int pageNumber,
                                                                      @RequestParam int pageSize) {
        return ResponseEntity.ok(servicesService.getAllVisibleServices(pageNumber, pageSize));
    }

    @Operation(
            summary = "GET SERVICE DETAILED",
            description = "Accepts service's id, and returns service's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service's information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-service-detailed/{serviceId}")
    public ResponseEntity<ServiceDetailed> getServiceDetailed(@PathVariable Long serviceId) {
        return ResponseEntity.ok(servicesService.getServiceDetailed(serviceId));
    }

    @Operation(
            summary = "GET SERVICE DETAILED FOR AUTHOR",
            description = "Accepts service's id, and returns service's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "User is not an author of this service"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-service-detailed-for-author/{serviceId}")
    public ResponseEntity<AuthorServiceDetailedDto> getServiceDetailedForAuthor(@PathVariable Long serviceId,
                                                                                Authentication authentication) {
        return ResponseEntity.ok(servicesService.getServiceDetailedForAuthor(serviceId, authentication));
    }

    @Operation(
            summary = "USER'S SERVICES",
            description = "Returns list of user's services",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/my-services")
    public ResponseEntity<AdvertisementPageDto> getUserServices(@RequestParam int pageNumber,
                                                                @RequestParam int pageSize,
                                                                Authentication authentication) {
        return ResponseEntity.ok(servicesService.getUserServices(pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "HIDE SERVICE",
            description = "Accepts service's id and then makes ad invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Service is already hidden | Only authors can hide their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/hide/{serviceId}")
    public ResponseEntity<String> hideService(@PathVariable Long serviceId, Authentication authentication) {
        return ResponseEntity.ok(servicesService.hideService(serviceId, authentication));
    }

    @Operation(
            summary = "SEND REQUEST TO SERVICE",
            description = "Accepts service id and user's information from jwt to leave a request to service",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User has left a request to service"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Service not found"),
                    @ApiResponse(responseCode = "409", description = "User can't respond to his/her own ad | User has been sent the request to this service already"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/send-request-to-service/{serviceId}")
    public ResponseEntity<String> sendRequestToService(@PathVariable Long serviceId, Authentication authentication) {
        return ResponseEntity.ok(servicesService.sendRequestToService(serviceId, authentication));
    }
}