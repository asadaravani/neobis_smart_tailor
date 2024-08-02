package kg.neobis.smarttailor.dtos.ads.list;

import java.io.Serializable;
import java.math.BigDecimal;

public record EquipmentListDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String authorFullName,
        String authorImageUrl
) implements Serializable {}