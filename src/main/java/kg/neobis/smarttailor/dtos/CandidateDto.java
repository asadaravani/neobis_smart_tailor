package kg.neobis.smarttailor.dtos;

public record CandidateDto(
        Long employeeId,
        String employeeFullName,
        String employeeEmail,
        String employeePhoneNumber,
        String organizationName
) {}