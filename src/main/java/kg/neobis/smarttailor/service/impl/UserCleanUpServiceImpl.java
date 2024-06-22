package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCleanUpServiceImpl {

    AppUserRepository appUserRepository;
    ConfirmationCodeRepository confirmationCodeRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupUnconfirmedUsers() {
        List<AppUser> unconfirmedUsers = appUserRepository.findAllByEnabledFalseAndCreatedAtBefore(LocalDateTime.now().minusDays(1));
        for (AppUser user : unconfirmedUsers) {
            confirmationCodeRepository.deleteByUser(user);
            appUserRepository.delete(user);
        }
    }
}
