package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.common.EndpointConstants;
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

    AuthenticationService authenticationService;

    @PostMapping("/confirmCode")
    public ResponseEntity<?> confirmCode(@RequestParam String email, @RequestParam Integer code) {
        return authenticationService.confirmCode(email, code);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(request));
    }

    @PostMapping("/resendConfirmationCode")
    public ResponseEntity<?> resendConfirmationCode(@RequestParam String email) {
        authenticationService.resendConfirmationCode(email);
        return ResponseEntity.ok("Confirmation code has been resent to the email: " + email);
    }
}