package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record AuthorServiceDetailedDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        String authorImage,
        String authorFullName,
        List<String> serviceImages,
        List<UserDto> serviceApplicants
) implements Serializable {}