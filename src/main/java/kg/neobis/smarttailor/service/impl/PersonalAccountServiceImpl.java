package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.PersonalAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountServiceImpl implements PersonalAccountService {

    AppUserService userService;
    CloudinaryService cloudinaryService;

    @Override
    public UserProfileDto getUserProfile(String email) {
        AppUser user = userService.findUserByEmail(email);
        return AppUserMapper.INSTANCE.toUserProfileDto(user);
    }

    @Override
    public void uploadProfileImage(MultipartFile file, String email){
        AppUser user = userService.findUserByEmail(email);
        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!user.getImage().getUrl().isEmpty()){
            try {
                cloudinaryService.deleteImage(user.getImage().getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        user.getImage().setUrl(imageUrl);
        userService.save(user);
    }

    @Override
    public void editProfile(UserProfileEditRequest request, String email){
        AppUser updatedUser = AppUserMapper.INSTANCE.updateProfile(request, userService.findUserByEmail(email));
        userService.save(updatedUser);
    }
}