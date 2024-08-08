package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderRequestDto(
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        LocalDate dateOfExecution,
        List<OrderItemDto> items
) implements Serializable {}