package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.service.AppUserService;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "App User")
@RequestMapping(EndpointConstants.API_USER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserController {

    AppUserService service;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdmin request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAdmin(request));
    }
}