package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record EmployeeListDto(
        Long id,
        String fullName,
        String email,
        List<EmployeeOrderListDto> orders,
        String position
) implements Serializable {}