package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.service.PersonalAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Personal Account")
@RequestMapping(EndpointConstants.PERSONAL_ACCOUNT_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountController {

    PersonalAccountService service;

    @Operation(summary = "User Profile", description = "May change to token or Id")
    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication) {
        return service.getUserProfile(authentication.getName());
    }

    @SneakyThrows
    @PostMapping("/profile/uploadImage")
    public void uploadProfileImage(@RequestParam("file") MultipartFile file, Authentication authentication){
        service.uploadProfileImage(file, authentication.getName());
    }
}