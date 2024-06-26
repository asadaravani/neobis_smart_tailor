package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Subscription;

import java.time.LocalDateTime;

public interface SubscriptionService {

    Subscription findSubscriptionByAppUser(AppUser user);

    LocalDateTime getSubscriptionExpiryTime(AppUser user);
}
