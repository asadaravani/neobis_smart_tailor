package kg.neobis.smarttailor.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileDto {
    Long id;
    String imagePath;
    String name;
    String surname;
    String patronymic;
    String email;
    String phoneNumber;
    LocalDate subscriptionExpirationDate;
}