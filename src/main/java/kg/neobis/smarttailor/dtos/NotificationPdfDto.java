package kg.neobis.smarttailor.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NotificationPdfDto(
        String customer,
        String customerEmail,
        String equipmentName,
        BigDecimal price

) {
}
