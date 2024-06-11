package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Image extends BaseEntity{

    @Column
    String url;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn
    CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn
    Service service;
}
