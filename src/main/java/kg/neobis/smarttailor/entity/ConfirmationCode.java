package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ConfirmationCode extends BaseEntity{

    @Column(nullable = false)
    Integer code;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdTime;

    @Column
    LocalDateTime confirmedAt;

    @Column(nullable = false)
    LocalDateTime expiryTime;

    @ManyToOne @JoinColumn
    AppUser user;
}
