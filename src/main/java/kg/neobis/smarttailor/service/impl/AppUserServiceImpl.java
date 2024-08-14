package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.exception.OutOfDateException;
import kg.neobis.smarttailor.exception.UnauthorizedException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserServiceImpl implements AppUserService {

    AppUserRepository appUserRepository;
    EntityManager entityManager;
    EmailService emailService;
    OrganizationEmployeeService organizationEmployeeService;
    SubscriptionTokenService subscriptionTokenService;

    @Override
    public String confirmSubscriptionRequest(String subscriptionConfirmationToken) {

        SubscriptionToken token = subscriptionTokenService.findByToken(subscriptionConfirmationToken);
        if (LocalDateTime.now().isBefore(token.getExpirationTime())) {
            AppUser user = findUserByEmail(token.getUser().getEmail());
            user.setHasSubscription(true);
            appUserRepository.save(user);
            subscriptionTokenService.delete(token);
            return "Subscription for the user \"".concat(user.getEmail()).concat("\" has been activated");
        } else {
            throw new OutOfDateException("Token has been expired");
        }
    }

    @Override
    public Boolean existsUserByEmail(String email) {
        return appUserRepository.existsUserByEmail(email);
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: ".concat(email)));
    }

    @Override
    public AppUser findUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: ".concat(String.valueOf(id))));
    }

    @Override
    public AppUser getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUser appUser) {
                return entityManager.find(AppUser.class, appUser.getId());
            } else {
                throw new IllegalArgumentException("Principal is not an instance of AppUser");
            }
        }
        throw new UnauthorizedException("Authentication required!");
    }

    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public String sendSubscriptionRequest(Authentication authentication) throws MessagingException {

        AppUser user = getUserFromAuthentication(authentication);

        if (organizationEmployeeService.existsByEmployeeEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("User is a member of another organization");
        }
        if (user.getHasSubscription()) {
            throw new ResourceAlreadyExistsException("User already has a subscription");
        }
        SubscriptionToken subscriptionToken = subscriptionTokenService.generateSubscriptionRequestToken(user);
        MimeMessage message = emailService.createSubscriptionRequestMail(user, subscriptionToken);
        emailService.sendEmail(message);

        return "Subscription request has been sent";
    }
}