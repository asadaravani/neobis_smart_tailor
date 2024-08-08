package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderListDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        LocalDate dateOfExecution,
        String imageUrl,
        String authorFullName,
        String authorImageUrl
) implements Serializable {}