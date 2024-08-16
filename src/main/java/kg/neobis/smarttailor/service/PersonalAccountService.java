package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PersonalAccountService {

    String editProfile(UserProfileEditRequest request, Authentication authentication);

    AdvertisementPageDto getUserAdvertisements(int pageNumber, int pageSize, Authentication authentication);

    UserProfileDto getUserProfile(Authentication authentication);

    AdvertisementPageDto getUserPurchases(int pageNumber, int pageSize, Authentication authentication);

    String uploadProfileImage(MultipartFile file, Authentication authentication) throws IOException;
}