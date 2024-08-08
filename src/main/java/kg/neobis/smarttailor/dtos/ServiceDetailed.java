package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record ServiceDetailed(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        String authorImage,
        String authorFullName,
        List<String> serviceImages
) implements Serializable {}