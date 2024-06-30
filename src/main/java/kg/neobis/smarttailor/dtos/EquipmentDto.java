package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link kg.neobis.smarttailor.entity.Equipment}
 */
public record EquipmentDto(Long id, String name, BigDecimal price, List<String> equipmentImages, String authorImage, String authorFullName,
                           String description, String contactInfo, Integer quantity) implements Serializable {
}