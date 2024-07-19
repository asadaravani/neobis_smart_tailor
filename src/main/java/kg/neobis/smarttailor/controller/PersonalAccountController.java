package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.service.PersonalAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @Operation(summary = "Set an avatar",
            description = "Sets an image to User, removes previous image from the Cloudinary")
    public void uploadProfileImage(@RequestParam("file") MultipartFile file, Authentication authentication){
        service.uploadProfileImage(file, authentication.getName());
    }

    @PutMapping("/profile/edit")
    @Operation(summary = "Edits User's Profile Info",
            description = "It does not validate or check any fields of the request, but sets them directly")
    public void editProfile(@RequestBody UserProfileEditRequest request, Authentication authentication){
        service.editProfile(request, authentication.getName());
    }

    @GetMapping("/my-advertisements/{pageNo}/{pageSize}")
    @Operation(summary = "My Advertisements",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "403", description = "authentication required"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    public List<MyAdvertisement> getMyAdvertisements(@PathVariable int pageNo, @PathVariable int pageSize,
                                                     Authentication authentication){
        return service.getUserAds(pageNo, pageSize, authentication.getName());
    }
}