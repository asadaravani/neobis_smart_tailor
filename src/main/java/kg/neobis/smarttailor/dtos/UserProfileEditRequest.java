package kg.neobis.smarttailor.dtos;

import lombok.Builder;
import java.io.Serializable;

@Builder
public record UserProfileEditRequest(
        String name,
        String surname,
        String patronymic,
        String phoneNumber
) implements Serializable { }
