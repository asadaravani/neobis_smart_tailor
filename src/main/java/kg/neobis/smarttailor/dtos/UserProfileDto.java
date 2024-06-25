package kg.neobis.smarttailor.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
    LocalDateTime expiryTime;
}
