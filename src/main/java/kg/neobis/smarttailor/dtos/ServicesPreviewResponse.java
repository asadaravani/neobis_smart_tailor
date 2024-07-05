package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record ServicesPreviewResponse (
    Long id,
    String imagePath,
    String name,
    String description,
    BigDecimal price,
    String authorImagePath,
    String authorName,
    String authorSurname,
    String patronymic
) implements Serializable {}
