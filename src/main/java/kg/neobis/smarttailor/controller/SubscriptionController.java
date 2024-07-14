package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Subscription")
@RequestMapping(EndpointConstants.SUBSCRIPTION_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionController {

    SubscriptionService service;

    @RequestMapping(value="/confirm-subscription-request", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmSubscriptionRequest(@RequestParam("token")String subscriptionConfirmationToken) {
        return service.confirmSubscriptionRequest(subscriptionConfirmationToken);
    }

    @PostMapping("/send-subscription-request")
    public ResponseEntity<?> sendSubscriptionRequest(Authentication authentication) throws MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.sendSubscriptionRequest(authentication));
    }
}