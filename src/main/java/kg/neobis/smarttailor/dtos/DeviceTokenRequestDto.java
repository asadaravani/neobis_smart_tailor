package kg.neobis.smarttailor.dtos;

import lombok.Builder;

@Builder
public record DeviceTokenRequestDto(
        String token
) {
}
