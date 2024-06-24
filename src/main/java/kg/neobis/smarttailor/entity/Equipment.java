package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Equipment extends BaseEntity{

    @Column
    String name;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 5, max = 1000, message = "Content must be between 5 and 1000 characters")
    String description;

    @Column(nullable = false)
    BigDecimal price;

    @OneToMany
    @JoinColumn
    List<Image> images;
}