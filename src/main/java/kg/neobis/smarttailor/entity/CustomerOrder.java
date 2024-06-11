package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomerOrder extends BaseEntity{

    @Column(nullable = false)
    @Size(min = 5, max = 255, message = "Name must be between 5 and 1000 characters")
    String name;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    String description;

    @Column
    String sizes;

    @Column(nullable = false, updatable = false)
    LocalDateTime completionDeadline;

    @OneToMany(mappedBy = "customerOrder", orphanRemoval = true)
    List<Image> images;

    @ManyToOne
    @JoinColumn
    AppUser author;
}
