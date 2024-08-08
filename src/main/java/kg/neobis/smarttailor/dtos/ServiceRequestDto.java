package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public record ServiceRequestDto(
        String name,
        String description,
        BigDecimal price,
        String contactInfo
) implements Serializable {}