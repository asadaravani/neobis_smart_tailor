package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderDetailed(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        String authorImage,
        String authorFullName,
        List<String> orderImages,
        LocalDate dateOfExecution,
        List<OrderItemDto> orderItems
) implements Serializable {}