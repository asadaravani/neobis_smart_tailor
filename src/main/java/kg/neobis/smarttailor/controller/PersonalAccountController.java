package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.common.EndpointConstants;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Personal Account")
@RequestMapping(EndpointConstants.PERSONAL_ACCOUNT_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountController {
    AppUserService userService;
    @Operation(summary = "User Profile", description = "May change to token or Id")
    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication){
        return userService.getUserProfile(authentication.getName());
    }
}
