package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface SubscriptionService {

    ResponseEntity<?> confirmSubscriptionRequest(String subscriptionConfirmationToken);

    Boolean existsSubscriptionByUser(AppUser user);

    Subscription findSubscriptionByAppUser(AppUser user);

    LocalDate getSubscriptionExpiryTime(AppUser user);

    ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException;
}