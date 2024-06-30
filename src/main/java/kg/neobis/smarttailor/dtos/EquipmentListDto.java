package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * DTO for {@link kg.neobis.smarttailor.entity.Equipment}
 */
@Builder
public record EquipmentListDto(Long equipmentId, String equipmentPhotoUrl, String name, BigDecimal price, String authorImageUrl, String authorFullName, String description) implements Serializable {
}