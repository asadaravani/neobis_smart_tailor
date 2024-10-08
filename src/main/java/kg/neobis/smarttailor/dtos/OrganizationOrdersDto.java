package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.OrderStatus;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record OrganizationOrdersDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        OrderStatus status
) implements Serializable {}