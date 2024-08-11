package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.util.List;

public record EmployeePageDto(
        Long employeeId,
        String employeeFullName,
        List<?> orders,
        boolean isLast,
        Long totalCount
) implements Serializable {}