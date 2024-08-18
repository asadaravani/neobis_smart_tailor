package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.util.List;

public record EmployeesPageDto(List<?> advertisement, boolean isLast, Long totalCount) implements Serializable {}