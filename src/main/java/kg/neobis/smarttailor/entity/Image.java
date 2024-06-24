package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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

    @ManyToOne
    @JoinColumn
    CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn
    Service service;
}
