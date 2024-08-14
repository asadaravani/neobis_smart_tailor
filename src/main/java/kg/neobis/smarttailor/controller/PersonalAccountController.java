package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.service.PersonalAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Personal Account")
@RequestMapping(EndpointConstants.PERSONAL_ACCOUNT_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountController {

    PersonalAccountService personalAccountService;

    @Operation(
            summary = "EDIT USER'S INFORMATION",
            description = "Accepts changed data and saves it",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's data has been changed"),
                    @ApiResponse(responseCode = "400", description = "Entered data has not been validated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@Valid @RequestBody UserProfileEditRequest request,
                                         BindingResult result,
                                         Authentication authentication) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + result.getAllErrors());
        }
        return ResponseEntity.ok(personalAccountService.editProfile(request, authentication));
    }

    @Operation(
            summary = "USER'S INFORMATION",
            description = "Returns user's personal information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(personalAccountService.getUserProfile(authentication));
    }

    @Operation(
            summary = "USER'S ADVERTISEMENTS",
            description = "Returns advertisements created by user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's advertisements received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/my-advertisements")
    public ResponseEntity<AdvertisementPageDto> getMyAdvertisements(@RequestParam int pageNumber,
                                                                    @RequestParam int pageSize,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(personalAccountService.getUserAdvertisements(pageNumber, pageSize, authentication));
    }

    @SneakyThrows
    @Operation(
            summary = "UPLOAD PROFILE IMAGE",
            description = "Sets an image to user, removes previous image from the Cloudinary",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile image has been uploaded"),
                    @ApiResponse(responseCode = "400", description = "Current request is not a multipart request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/profile/uploadImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file,
                                                Authentication authentication) {
        return ResponseEntity.ok(personalAccountService.uploadProfileImage(file, authentication));
    }
}