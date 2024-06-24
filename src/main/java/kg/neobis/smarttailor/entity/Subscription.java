package kg.neobis.smarttailor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Subscription extends BaseEntity{

    @OneToOne
    AppUser user;
}