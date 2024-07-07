package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.ServiceDetailedResponse;
import kg.neobis.smarttailor.dtos.ServicesPreviewResponse;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Service")
@RequestMapping(EndpointConstants.SERVICE_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceController {

    ServicesService servicesService;

    @Operation(
            summary = "Get Service by id",
            description = "Get detailed info of the Service",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Empty list"),
            }
    )
    @GetMapping("/{id}")
    public ServiceDetailedResponse getServiceDetailed(@PathVariable Long id){
        return servicesService.getServiceDetailed(id);
    }

    @Operation(
            summary = "Get all services",
            description = "Requires parameters for the Pagination. Does NOT return 404, but empty list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Services' list"),
                    @ApiResponse(responseCode = "500", description = "Integral server error")
            }
    )
    @GetMapping
    public List<ServicesPreviewResponse> getAllServices(@RequestParam int pageNo,
                                                        @RequestParam int pageSize){
        return servicesService.getServices(pageNo, pageSize);
    }

    @Operation(
            summary = "Add service",
            description = "Gets and sets the Author of an added Service from the token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "403", description = "Authentication Required"),
                    @ApiResponse(responseCode = "500", description = "Integral server error")
            }
    )
    @PostMapping("/addService")
    public String addService(@RequestPart("dto") String dto,
                             @RequestPart("photos") List<MultipartFile> photos,
                             Authentication authentication){
        return servicesService.addService(dto, photos, authentication.getName());
    }

    @Operation(
            summary = "It is not Available right now",
            description = "Fixing...",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "403", description = "Authentication Required"),
                    @ApiResponse(responseCode = "500", description = "Integral server error")
            }
    )
    @DeleteMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id){
        return servicesService.deleteServiceById(id);
    }

}
