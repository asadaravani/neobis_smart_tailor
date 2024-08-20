package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "App User")
@RequestMapping(EndpointConstants.APP_USER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserController {

    AppUserService appUserService;

    @Operation(
            summary = "CONFIRM SUBSCRIPTION REQUEST",
            description = "Activates subscription (changes user's field 'hasSubscription' value to 'true)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription has been activated for user"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "410", description = "Token has been expired"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @RequestMapping(value="/confirm-subscription-request", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmSubscriptionRequest(@RequestParam("token")String subscriptionConfirmationToken) {
        return ResponseEntity.ok(appUserService.confirmSubscriptionRequest(subscriptionConfirmationToken));
    }

    @Operation(
            summary = "SEND SUBSCRIPTION REQUEST",
            description = "Accepts user's data from jwt, and then sends the subscription request to the admin email address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription request has been sent"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "409", description = "User already has a subscription | User is a member of another organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/send-subscription-request")
    public ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException {
        return ResponseEntity.ok(appUserService.sendSubscriptionRequest(authentication));
    }
}