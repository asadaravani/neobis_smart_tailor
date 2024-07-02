package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Column(nullable = false)
    @Size(min = 5, max = 250)
    String name;

    @Column(nullable = false)
    @Size(min = 5, max = 1000)
    String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_images",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Size(max = 5, message = "Maximum number of images: 5")
    List<Image> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_sizes",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    List<kg.neobis.smarttailor.entity.Size> sizes = new ArrayList<>();

    @Column(nullable = false)
    LocalDateTime termOfExecution;

    @Column(nullable = false)
    String contactInfo;

    @Column(nullable = false)
    Integer price;

    @ManyToOne
    @JoinColumn
    AppUser author;
}