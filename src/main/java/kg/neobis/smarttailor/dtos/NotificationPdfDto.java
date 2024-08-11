package kg.neobis.smarttailor.dtos;

import java.math.BigDecimal;

public record NotificationPdfDto(
        String customer,
        String customerEmail,
        String equipmentName,
        BigDecimal price
) {}