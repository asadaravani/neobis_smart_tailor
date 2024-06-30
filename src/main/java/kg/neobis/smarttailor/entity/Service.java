package kg.neobis.smarttailor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Service extends BaseEntity{

    @Column(nullable = false)
    @Size(min = 5, max = 255, message = "Name must be between 5 and 1000 characters")
    String name;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    String description;

    @ManyToOne
    AppUser author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "service_images",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    List<Image> images = new ArrayList<>();


    @Column
    BigInteger price;
}