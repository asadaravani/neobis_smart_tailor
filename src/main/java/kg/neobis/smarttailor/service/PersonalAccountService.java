package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.dtos.MyAdvertisement;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PersonalAccountService {

    String editProfile(UserProfileEditRequest request, Authentication authentication);

    List<MyAdvertisement> getUserAdvertisements(int pageNumber, int pageSize, Authentication authentication);

    UserProfileDto getUserProfile(Authentication authentication);

    String uploadProfileImage(MultipartFile file, Authentication authentication) throws IOException;
}