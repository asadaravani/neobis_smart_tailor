package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationDto(
        String title,
        String description,
        LocalDateTime time
) {
}
