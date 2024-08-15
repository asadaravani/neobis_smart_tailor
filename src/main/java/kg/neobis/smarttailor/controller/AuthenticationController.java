package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping(EndpointConstants.AUTH_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @Operation(
            summary = "EMAIL CONFIRMATION",
            description = "Accepts email and confirmation code, and then sends access and refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access and refresh tokens have been received"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "404", description = "Invalid confirmation code | User not found with specified email | Confirmation code not found for user with specified email"),
                    @ApiResponse(responseCode = "410", description = "Confirmation code has been expired"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/confirm-email")
    public ResponseEntity<LoginResponse> confirmEmail(@RequestParam String email,
                                                      @RequestParam Integer code) {
        return ResponseEntity.ok(authenticationService.confirmEmail(email, code));
    }

    @Operation(
            summary = "LOGIN",
            description = "Accepts email, and then sends the confirmation code to the specified email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Confirmation code has been sent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Required parameter 'email' is not present"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email) {
        return ResponseEntity.ok(authenticationService.login(email));
    }

    @Operation(
            summary = "LOG OUT",
            description = "Accepts access token and adds it to black list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Log out completed"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.logOut(request));
    }

    @Operation(
            summary = "REFRESH TOKEN",
            description = "Accepts refresh token, and then generates new access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token has been generated and received"),
                    @ApiResponse(responseCode = "400", description = "Required parameter 'refreshToken' is not present"),
                    @ApiResponse(responseCode = "404", description = "Refresh token not found"),
                    @ApiResponse(responseCode = "410", description = "Refresh token has been expired"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }

    @Operation(
            summary = "RESEND CONFIRMATION CODE",
            description = "Accepts email, and then sends confirmation code to specified email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Confirmation code has been sent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Required parameter 'email' is not present"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/resend-confirmation-code")
    public ResponseEntity<?> resendConfirmationCode(@RequestParam String email) throws MessagingException {
        return ResponseEntity.ok(authenticationService.resendConfirmationCode(email));
    }

    @Operation(
            summary = "REGISTRATION",
            description = "Accepts user's data, saves it in database, generates confirmation code and sends it to the specified email",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User's data saved in database. Confirmation code has been sent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Entered data has not been validated"),
                    @ApiResponse(responseCode = "409", description = "User with specified email already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request,
                                    BindingResult result) throws MessagingException {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(request));
    }
}