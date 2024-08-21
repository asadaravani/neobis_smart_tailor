package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record OrganizationOrderHistoryDetailedDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        LocalDate dateOfCompletion,
        List<EmployeeDto> employees,
        String authorFullName,
        String authorContactInfo
) {}