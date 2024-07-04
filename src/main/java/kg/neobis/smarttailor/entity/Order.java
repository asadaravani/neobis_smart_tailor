package kg.neobis.smarttailor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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

    @Column(nullable = false)
    LocalDate dateOfExecution;

    @Column(nullable = false)
    String contactInfo;

    @Column(nullable = false)
    Integer price;

    @ManyToOne
    @JoinColumn
    AppUser author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_order_item",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "order_item_id")
    )
    List<OrderItem> items = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_images",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Size(max = 5, message = "Maximum number of images: 5")
    List<Image> images = new ArrayList<>();
}