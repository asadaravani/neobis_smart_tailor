package kg.neobis.smarttailor.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUpRequest(

        @NotBlank(message = "Surname is required")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Surname should contain only letters")
        @Size(min = 2, max = 50)
        String surname,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50)
        String name,

        @NotBlank(message = "Patronymic is required")
        @Size(min = 2, max = 50)
        String patronymic,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(min = 10, max = 20)
        String phoneNumber
) {}