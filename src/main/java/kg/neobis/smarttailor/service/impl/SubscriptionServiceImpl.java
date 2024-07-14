package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Subscription;
import kg.neobis.smarttailor.entity.SubscriptionToken;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.repository.SubscriptionRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.SubscriptionTokenService;
import kg.neobis.smarttailor.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    AppUserService appUserService;
    EmailService emailService;
    SubscriptionTokenService subscriptionTokenService;
    SubscriptionRepository subscriptionRepository;

    @Override
    public ResponseEntity<?> confirmSubscriptionRequest(String subscriptionConfirmationToken) {

        SubscriptionToken token = subscriptionTokenService.findByToken(subscriptionConfirmationToken);

        if (LocalDateTime.now().isBefore(token.getExpirationTime())) {
            Subscription subscription = Subscription.builder()
                    .user(token.getUser())
                    .expirationDate(LocalDate.now().plusYears(1))
                    .build();
            subscriptionRepository.save(subscription);
            return ResponseEntity.ok("subscription for the user \"".concat(token.getUser().getEmail()).concat("\" issued"));
        }

        return ResponseEntity.badRequest().body("Token has expired. Try to resend the request.");
    }

    @Override
    public Boolean existsSubscriptionByUser(AppUser user) {
        return subscriptionRepository.existsSubscriptionByUser(user);
    }

    @Override
    public Subscription findSubscriptionByAppUser(AppUser user) {
        return subscriptionRepository.findByUser(user).orElse(null);
    }

    @Override
    public LocalDate getSubscriptionExpiryTime(AppUser user) {
        Subscription subscription = findSubscriptionByAppUser(user);
        if (subscription == null) {
            return null;
        }
        return subscription.getExpirationDate();
    }

    @Override
    public ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Subscription subscription = findSubscriptionByAppUser(user);
        if (subscription != null) {
            if (LocalDate.now().isBefore(subscription.getExpirationDate())) {
                throw new ResourceAlreadyExistsException("user already has a subscription, which expires on ".concat("" + subscription.getExpirationDate()), HttpStatus.CONFLICT.value());
            } else {
                subscriptionRepository.delete(subscription);
                subscriptionTokenService.deleteByUser(user);
            }
        }
        SubscriptionToken subscriptionToken = subscriptionTokenService.generateSubscriptionRequestToken(user);
        MimeMessage message = emailService.createSubscriptionRequestMail(user, subscriptionToken);
        emailService.sendEmail(message);

        return ResponseEntity.ok("Hooray! A subscription is on the way. Our administrator will contact you");
    }
}