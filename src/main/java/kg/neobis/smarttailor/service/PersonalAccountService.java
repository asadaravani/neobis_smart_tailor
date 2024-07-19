package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PersonalAccountService {

    UserProfileDto getUserProfile(String email);

    void uploadProfileImage(MultipartFile file, String email) throws IOException;

    void editProfile(UserProfileEditRequest request, String email);

    List<MyAdvertisement> getUserAds(int pageNo, int pageSize, String email);
}