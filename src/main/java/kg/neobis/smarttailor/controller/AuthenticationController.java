package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.LoginAdmin;
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
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping(EndpointConstants.AUTH_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService service;

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam String email, @RequestParam Integer code) {
        return service.confirmEmail(email, code);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(email));
    }

    @PostMapping("/login-admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginAdmin request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.loginAdmin(request));
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        return ResponseEntity.ok(service.logOut(request));
    }

    @PostMapping("/resend-confirmation-code")
    public ResponseEntity<?> resendConfirmationCode(@RequestParam String email) throws MessagingException {
        service.resendConfirmationCode(email);
        return ResponseEntity.ok("Confirmation code has been resent to the email: " + email);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request, BindingResult result) throws MessagingException {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signUp(request));
    }
}