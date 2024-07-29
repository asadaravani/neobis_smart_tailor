package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "App User")
@RequestMapping(EndpointConstants.APP_USER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserController {

    AppUserService service;

    @RequestMapping(value="/confirm-subscription-request", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmSubscriptionRequest(@RequestParam("token")String subscriptionConfirmationToken) {
        return ResponseEntity.status(HttpStatus.OK).body(service.confirmSubscriptionRequest(subscriptionConfirmationToken));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdmin request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAdmin(request));
    }

    @Operation(
            summary = "SEND SUBSCRIPTION REQUEST",
            description = "The method accepts user data from jwt, and then sends the subscription request to the admin email address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription request has been sent"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "409", description = "User already has a subscription | User is a member of another organization"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/send-subscription-request")
    public ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(service.sendSubscriptionRequest(authentication));
    }
}