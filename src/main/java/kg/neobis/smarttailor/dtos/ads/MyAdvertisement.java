package kg.neobis.smarttailor.dtos.ads;

import kg.neobis.smarttailor.enums.AdvertType;
import lombok.Builder;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record MyAdvertisement(
        Long id,
        String imagePath,
        AdvertType type,
        String name,
        String description,
        LocalDateTime createdAt
) implements Serializable {
}
