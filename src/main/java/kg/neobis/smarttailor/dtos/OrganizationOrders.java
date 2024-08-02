package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrganizationOrders(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) implements Serializable {
}
