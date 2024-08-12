package kg.neobis.smarttailor.dtos;

import lombok.Builder;

@Builder
public record FirebaseNotificationResponse(

        int status,
        String message

) {
}
