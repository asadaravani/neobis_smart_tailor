package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationCode extends BaseEntity {

    @Column
    Integer code;

    @Column
    LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn
    AppUser user;

    public ConfirmationCode() {
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}