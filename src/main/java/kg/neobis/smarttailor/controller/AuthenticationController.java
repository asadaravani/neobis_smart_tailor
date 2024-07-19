package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.LoginAdmin;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.exception.UnauthorizedException;
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

import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping(EndpointConstants.AUTH_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService service;

    @Operation(
            summary = "EMAIL CONFIRMATION",
            description = "User enters an email and 4-digit code that was sent to the mail, and if the data is valid, receives access and refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data is valid. Access and refresh tokens received successfully"),
                    @ApiResponse(responseCode = "400", description = "Confirmation code has expired"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "404", description = "Invalid confirmation code"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/confirm-email")
    public LoginResponse confirmEmail(@RequestParam String email, @RequestParam Integer code) {
        return service.confirmEmail(email, code);
    }

    @Operation(
            summary = "LOGIN",
            description = "User enters an email address and receives 4-digit confirmation code at specified email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Confirmation code has been sent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(email));
    }

    @Operation(
            summary = "LOGIN FOR ADMIN",
            description = "Admin enters an email and password, and if the data is valid, receives access and refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data is valid. Access and refresh tokens received successfully"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email or/and password"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/login-admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginAdmin request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.loginAdmin(request));
    }

    @Operation(
            summary = "LOG OUT",
            description = "Authorized user's jwt is added to the blacklist",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Log out completed"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        return ResponseEntity.ok(service.logOut(request));
    }

    @Operation(
            summary = "REFRESH TOKEN",
            description = "Changing access token using the refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token changed"),
                    @ApiResponse(responseCode = "400", description = "Refresh token expired"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Refresh token not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            Map<String, String> tokens = service.refreshToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
            summary = "RESEND CONFIRMATION CODE",
            description = "User enters the data required for registration and receives 4-digit confirmation code at specified email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Confirmation code has been resent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Required parameter 'email' is not present"),
                    @ApiResponse(responseCode = "404", description = "User not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/resend-confirmation-code")
    public ResponseEntity<?> resendConfirmationCode(@RequestParam String email) throws MessagingException {
        return ResponseEntity.ok(service.resendConfirmationCode(email));
    }

    @Operation(
            summary = "REGISTRATION",
            description = "User enters the data required for registration and receives 4-digit confirmation code at specified email",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Data saved in database. Confirmation code has been sent to the specified email"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present or entered data has not been validated"),
                    @ApiResponse(responseCode = "409", description = "Specified email is already in user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request, BindingResult result) throws MessagingException {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signUp(request));
    }
}