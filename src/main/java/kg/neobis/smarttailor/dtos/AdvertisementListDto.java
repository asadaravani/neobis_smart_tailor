package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AdvertType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdvertisementListDto(
        AdvertType type,
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String authorFullName,
        String authorImageUrl,
        LocalDateTime updatedAt
) implements Serializable {}