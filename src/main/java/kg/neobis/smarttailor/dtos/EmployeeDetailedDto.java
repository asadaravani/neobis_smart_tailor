package kg.neobis.smarttailor.dtos;

import java.io.Serializable;

public record EmployeeDetailedDto(
        Long id,
        String imagePath,
        String name,
        String surname,
        String patronymic,
        String email,
        String phoneNumber,
        String positionName
) implements Serializable {}