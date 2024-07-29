package kg.neobis.smarttailor.dtos.ads.list;

import java.io.Serializable;
import java.math.BigDecimal;

public record ServiceListDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String equipmentImageUrl,
    String authorFullName,
    String authorImageUrl
) implements Serializable {}