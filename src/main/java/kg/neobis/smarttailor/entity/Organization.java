package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@SuperBuilder
public class Organization extends BaseEntity{

    @Column
    String imagePath;

    @Column(nullable = false)
    @Size(min = 5, max = 255, message = "Name must be between 5 and 1000 characters")
    String name;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    String description;

    @OneToMany
    @JoinTable(
            name = "organization_employees",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id"))
    List<AppUser> organizationEmployees;
}