package kg.neobis.smarttailor.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EmployeeOrderListDto(
        Long id,
        String name,
        String description,
        LocalDate dateOfStart,
        BigDecimal price,
        List<EmployeeDto> employees,
        String authorFullName,
        String authorImage,
        String authorContactInfo
) {}