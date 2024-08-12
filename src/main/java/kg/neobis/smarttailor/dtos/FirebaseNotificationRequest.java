package kg.neobis.smarttailor.dtos;

import lombok.Builder;

@Builder
public record FirebaseNotificationRequest(
        String title,
        String body,
        String topic,
        String token
) {
}
