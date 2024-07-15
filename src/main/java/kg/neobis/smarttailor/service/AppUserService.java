package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface AppUserService {

    ResponseEntity<?> confirmSubscriptionRequest(String subscriptionConfirmationToken);

    ResponseEntity<?> createAdmin(CreateAdmin request);

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser findUserByEmail(String email);

    AppUser getUserFromAuthentication(Authentication authentication);

    AppUser save(AppUser appUser);

    ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException;
}