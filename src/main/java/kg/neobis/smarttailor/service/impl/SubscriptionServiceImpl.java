package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Subscription;
import kg.neobis.smarttailor.repository.SubscriptionRepository;
import kg.neobis.smarttailor.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription findSubscriptionByAppUser(AppUser user){
        return subscriptionRepository.findByUser(user).orElse(null);
    }

    @Override
    public LocalDateTime getSubscriptionExpiryTime(AppUser user){
        Subscription subscription = findSubscriptionByAppUser(user);
        if(subscription == null){
            return null;
        }
        else {
            return subscription.getExpiryTime();
        }
    }
}
