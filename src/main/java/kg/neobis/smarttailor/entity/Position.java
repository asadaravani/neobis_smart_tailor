package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kg.neobis.smarttailor.enums.AccessRight;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position extends BaseEntity {

    @Column(nullable = false)
    String name;

    @ElementCollection(targetClass = AccessRight.class)
    @CollectionTable(name = "position_access_rights", joinColumns = @JoinColumn(name = "position_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "access_right")
    Set<AccessRight> accessRights;

    @ManyToOne
    @JoinColumn
    Organization organization;
}