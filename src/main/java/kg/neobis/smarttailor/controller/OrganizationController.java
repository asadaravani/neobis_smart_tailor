package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.OrganizationDetailed;
import kg.neobis.smarttailor.service.OrganizationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            summary = "ACCEPT INVITATION TO ORGANIZATION",
            description = "User who has been sent an invitation to join the organization can accept it by clicking on the button",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User accepted invitation"),
                    @ApiResponse(responseCode = "400", description = "Token has been expired"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @RequestMapping(value = "/accept-invitation", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> acceptInvitation(@RequestParam("token") String invitingToken) {
        return service.acceptInvitation(invitingToken);
    }

    @Operation(
            summary = "CREATE ORGANIZATION",
            description = "The method accepts organization's name, description and photo to create the organization",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Organization has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User has no subscription"),
                    @ApiResponse(responseCode = "409", description = "User already has organization | Organization with specified name already exists "),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/create-organization")
    public ResponseEntity<String> createOrganization(@RequestPart("organization") String organization,
                                                     @RequestPart("image") MultipartFile image,
                                                     Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrganization(organization, image, authentication));
    }

    @Operation(
            summary = "GET ORGANIZATION",
            responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User has no subscription "),
            }
    )
    @GetMapping
    public OrganizationDetailed getOrganization(Authentication authentication){
        return service.getOrganization(authentication.getName());
    }

    @Operation(
            summary = "SEND INVITATION TO EMPLOYEE",
            description = "The method accepts employee data, and then sends the invitation email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation has been sent"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User has no permission to invite employee | User has no organization | Specified position not found"),
                    @ApiResponse(responseCode = "409", description = "Employee has his own organization | Employee is already a member of another organization"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/send-invitation")
    public ResponseEntity<?> sendInvitation(@RequestPart("employee") String request, Authentication authentication) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(service.sendInvitation(request, authentication));
    }
}