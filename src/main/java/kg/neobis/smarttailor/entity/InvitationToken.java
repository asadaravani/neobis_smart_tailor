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

@Getter
@Setter
@Entity
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvitationToken extends BaseEntity {

    String token;

    @Column
    LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn
    AppUser user;

    @ManyToOne
    @JoinColumn
    Organization organization;

    @ManyToOne
    @JoinColumn
    Position position;

    public InvitationToken() {
        this.expirationTime = LocalDateTime.now().plusDays(1);
    }
}