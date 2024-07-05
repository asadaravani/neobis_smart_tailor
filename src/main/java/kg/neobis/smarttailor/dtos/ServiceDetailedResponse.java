package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record ServiceDetailedResponse (
    Long id,
    List<String> imagePaths,
    String name,
    String description,
    BigDecimal price,
    String authorImagePath,
    String authorName,
    String authorSurname,
    String patronymic,
    String contactInfo
) implements Serializable{}
