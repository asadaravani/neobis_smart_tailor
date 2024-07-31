package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AppUserService {

    String confirmSubscriptionRequest(String subscriptionConfirmationToken);

    Boolean existsUserByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser findUserByEmail(String email);

    AppUser getUserFromAuthentication(Authentication authentication);

    AppUser save(AppUser appUser);

    String sendSubscriptionRequest(Authentication authentication) throws MessagingException;
}