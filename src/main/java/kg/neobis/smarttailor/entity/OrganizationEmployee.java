package kg.neobis.smarttailor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "organization_employee",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "employee_id", "position_id"}))

public class OrganizationEmployee extends BaseEntity {

    @ManyToOne
    @JoinColumn
    AppUser employee;

    @ManyToOne
    @JoinColumn
    Position position;

    @ManyToOne
    @JoinColumn
    Organization organization;
}