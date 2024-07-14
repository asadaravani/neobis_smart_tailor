package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionToken extends BaseEntity {

    String token;

    @Column(nullable = false)
    LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn
    AppUser user;

    public SubscriptionToken() {
        this.expirationTime = LocalDateTime.now().plusMinutes(5);
    }
}