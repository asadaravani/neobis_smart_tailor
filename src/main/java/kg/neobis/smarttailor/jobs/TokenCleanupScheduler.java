package kg.neobis.smarttailor.jobs;

import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.BlackListTokenService;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import kg.neobis.smarttailor.service.InvitationTokenService;
import kg.neobis.smarttailor.service.RefreshTokenService;
import kg.neobis.smarttailor.service.SubscriptionTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenCleanupScheduler {

    AppUserService appUserService;
    BlackListTokenService blackListTokenService;
    ConfirmationCodeService confirmationCodeService;
    InvitationTokenService invitationTokenService;
    RefreshTokenService refreshTokenService;
    SubscriptionTokenService subscriptionTokenService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void cleanupBlackList() {
        blackListTokenService.deleteAll();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredConfirmationCodes() {
        confirmationCodeService.deleteExpiredCodes();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredInvitationTokens() {
        invitationTokenService.deleteExpiredTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredRefreshTokens() {
        refreshTokenService.deleteExpiredTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredSubscriptionTokens() {
        subscriptionTokenService.deleteExpiredTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupNotEnabledUsers() {
        appUserService.deleteNotEnabledUsers();
    }
}