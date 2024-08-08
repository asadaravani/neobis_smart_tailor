package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public record ServiceListDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imageUrl,
    String authorFullName,
    String authorImageUrl
) implements Serializable {}