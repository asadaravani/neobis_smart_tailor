package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import kg.neobis.smarttailor.enums.AccessRight;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Position extends BaseEntity{

    @Column(nullable = false)
    String name;

    @ElementCollection(targetClass = AccessRight.class)
    @CollectionTable(name = "position_access_rights", joinColumns = @JoinColumn(name = "position_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "access_right")
    List<AccessRight> accessRights;
}