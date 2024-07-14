package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.SubscriptionToken;

public interface SubscriptionTokenService {

    void delete(SubscriptionToken subscriptionToken);

    SubscriptionToken findByToken(String token);

    SubscriptionToken generateSubscriptionRequestToken(AppUser user);
}