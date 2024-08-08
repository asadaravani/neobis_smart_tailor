package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record EmployeeListDto(
        Long id,
        String fullName,
        String email,
        List<Long> orders,
        String position
) {}
