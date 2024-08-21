package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AccessRight;

import java.util.Set;

public record EmployeeCard (
    Long id,
    String fullName,
    String email,
    String position,
    int weight,
    Set<AccessRight> accessRights
) {}
