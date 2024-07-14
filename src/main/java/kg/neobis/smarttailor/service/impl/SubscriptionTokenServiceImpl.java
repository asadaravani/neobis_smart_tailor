package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.SubscriptionToken;
import kg.neobis.smarttailor.repository.SubscriptionTokenRepository;
import kg.neobis.smarttailor.service.SubscriptionTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionTokenServiceImpl implements SubscriptionTokenService {

    SubscriptionTokenRepository subscriptionTokenRepository;

    @Override
    public SubscriptionToken findByToken(String token) {
        return subscriptionTokenRepository.findByToken(token);
    }

    @Override
    public void deleteByUser(AppUser user) {
        SubscriptionToken subscriptionToken = subscriptionTokenRepository.findByUser(user);
        subscriptionTokenRepository.delete(subscriptionToken);
    }

    @Override
    public SubscriptionToken generateSubscriptionRequestToken(AppUser user) {

        SubscriptionToken subscriptionToken = new SubscriptionToken();
        String token = UUID.randomUUID().toString();
        subscriptionToken.setToken(token);
        subscriptionToken.setUser(user);
        subscriptionTokenRepository.save(subscriptionToken);

        return subscriptionToken;
    }
}