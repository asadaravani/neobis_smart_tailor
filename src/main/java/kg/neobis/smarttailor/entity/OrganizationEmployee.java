package kg.neobis.smarttailor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationEmployee extends BaseEntity {

    @OneToOne
    @JoinColumn
    AppUser employee;

    @OneToOne
    @JoinColumn
    Position position;

    @OneToOne
    @JoinColumn
    Organization organization;
}