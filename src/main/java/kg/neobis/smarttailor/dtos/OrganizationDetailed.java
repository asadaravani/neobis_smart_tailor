package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record OrganizationDetailed(
        Long id,
        String imagePath,
        String name,
        String description,
        LocalDateTime createdAt
) implements Serializable {
}
