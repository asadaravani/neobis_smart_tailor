package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AppUserService {

    ResponseEntity<?> createAdmin(CreateAdmin request);

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser findUserByEmail(String email);

    AppUser getUserFromAuthentication(Authentication authentication);

    AppUser save(AppUser appUser);
}