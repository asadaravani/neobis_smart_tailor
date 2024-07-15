package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.service.OrganizationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Organization")
@RequestMapping(EndpointConstants.ORGANIZATION_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationController {

    OrganizationService service;

    @Operation(
            summary = "creating organization",
            description = "authorized user transmits organization's name, description and photo to create the organization",
            responses = {
                    @ApiResponse(responseCode = "200", description = "organization has been created"),
                    @ApiResponse(responseCode = "403", description = "authentication required"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @PostMapping("/create-organization")
    public ResponseEntity<String> createOrganization(@RequestPart("organization") String organization,
                                           @RequestPart("image") MultipartFile image,
                                           Authentication authentication) {
        return ResponseEntity.ok(service.createOrganization(organization, image, authentication));
    }
}