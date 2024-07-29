package kg.neobis.smarttailor.dtos.ads.detailed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record EquipmentDetailed(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        String authorImage,
        String authorFullName,
        List<String> equipmentImages,
        Integer quantity
) implements Serializable {}