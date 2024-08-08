package kg.neobis.smarttailor.dtos;

import java.io.Serializable;

public record UserProfileDto(
        Long id,
        String imagePath,
        String name,
        String surname,
        String patronymic,
        String email,
        String phoneNumber,
        Boolean hasSubscription,
        Boolean inOrganization
) implements Serializable {}