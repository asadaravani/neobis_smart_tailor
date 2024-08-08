package kg.neobis.smarttailor.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record UserProfileEditRequest(

        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @NotBlank
        @Size(min = 2, max = 50)
        String surname,

        @NotBlank
        @Size(min = 2, max = 50)
        String patronymic,

        @NotBlank
        @Size(min = 10, max = 30)
        String phoneNumber
) implements Serializable { }
