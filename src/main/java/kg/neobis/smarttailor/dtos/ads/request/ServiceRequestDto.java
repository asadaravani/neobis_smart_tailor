package kg.neobis.smarttailor.dtos.ads.request;

import java.io.Serializable;
import java.math.BigDecimal;

public record ServiceRequestDto(
        String name,
        String description,
        BigDecimal price,
        String contactInfo
) implements Serializable {}