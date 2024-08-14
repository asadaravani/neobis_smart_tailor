package kg.neobis.smarttailor.dtos;

public record EmployeeInvitationRequest(
        String surname,
        String name,
        String patronymic,
        String email,
        String phoneNumber,
        String position
) {}