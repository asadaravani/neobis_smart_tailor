package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.AppUser;
import org.springframework.security.core.Authentication;

public interface AppUserService {

    String confirmSubscriptionRequest(String subscriptionConfirmationToken);

    Boolean existsUserByEmail(String email);

    AppUser findUserByEmail(String email);

    AppUser findUserById(Long id);

    AppUser getUserFromAuthentication(Authentication authentication);

    AppUser save(AppUser appUser);

    String sendSubscriptionRequest(Authentication authentication) throws MessagingException;
}