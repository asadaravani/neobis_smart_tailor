package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UserOrderHistoryDto(
        Long id,
        String name,
        BigDecimal price,
        OrderStatus status,
        LocalDate date
) {}