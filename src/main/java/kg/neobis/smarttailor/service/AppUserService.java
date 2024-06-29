package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface AppUserService {

    ResponseEntity<?> createAdmin(CreateAdmin request);

    Boolean existsUserByEmail(String email);

    UserProfileDto getUserProfile(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser findUserByEmail(String email);

    AppUser save(AppUser appUser);
}