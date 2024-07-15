package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PersonalAccountService {

    UserProfileDto getUserProfile(String email);

    void uploadProfileImage(MultipartFile file, String email) throws IOException;

    void editProfile(UserProfileEditRequest request, String email);
}