package kg.neobis.smarttailor.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpRequest {

    @NotBlank(message = "Surname is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Surname should contain only letters")
    @Size(min = 2, max = 50)
    String surname;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name should contain only letters")
    @Size(min = 2, max = 50)
    String name;

    @NotBlank(message = "Patronymic is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Patronymic should contain only letters")
    @Size(min = 3, max = 30)
    String patronymic;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$", message = "Email should contain only letters and digits before @")
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+][0-9]+$", message = "Phone number should contain only country's code and abonent's numbers. Don't use spaces")
    @Size(min = 10, max = 16)
    String phoneNumber;
}