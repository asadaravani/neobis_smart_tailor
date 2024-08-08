package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public record EquipmentRequestDto(
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        Integer quantity
) implements Serializable {}