package kg.neobis.smarttailor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Advertisement extends BaseEntity {

    @Column(nullable = false)
    @Size(min = 5, max = 250)
    String name;

    @Column(nullable = false)
    @Size(min = 5, max = 1000)
    String description;

    @Column(nullable = false)
    BigDecimal price;

    @Column(nullable = false)
    String contactInfo;

    @Column
    Boolean isVisible;

    @ManyToOne
    @JoinColumn
    AppUser author;

    public String getAuthorImageUrl(Advertisement advertisement) {
        return (advertisement.getAuthor() != null && advertisement.getAuthor().getImage() != null) ?
                advertisement.getAuthor().getImage().getUrl() : "";
    }

    public String getFirstImage(List<Image> images) {
        return (images != null && !images.isEmpty()) ? images.get(0).getUrl() : "";
    }

    public String getFullName(Advertisement advertisement) {

        if (advertisement == null || advertisement.getAuthor() == null) {
            return "";
        }
        AppUser author = advertisement.getAuthor();
        String name = author.getName() != null ? author.getName() : "";
        String surname = author.getSurname() != null ? author.getSurname() : "";
        String patronymic = author.getPatronymic() != null ? author.getPatronymic() : "";
        return String.format("%s %s %s", surname, name, patronymic);
    }
}