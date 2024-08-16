package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record CurrentOrderDetailed(
        Long id,
        String name,
        String description,
        BigDecimal price,
        OrderStatus status,
        LocalDate dateOfExecution,
        List<String> images,
        List<EmployeeDto> employees,
        String authorFullName,
        String authorImage,
        String authorContactInfo
) {}