package kg.neobis.smarttailor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationCode extends BaseEntity {

    Integer code;
    LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn
    AppUser user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}