package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link kg.neobis.smarttailor.entity.Equipment}
 */
public record EquipmentRequestDto(
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        String contactInfo
) implements Serializable {
}