package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record EmployeeStageOrderListDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    LocalDate date,
    List<EmployeeDto> employees,
    String authorFullName,
    String authorImage,
    String authorContactInfo
) {}