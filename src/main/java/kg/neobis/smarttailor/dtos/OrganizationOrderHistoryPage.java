package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.OrderStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record OrganizationOrderHistoryPage(
    Long id,
    String name,
    String description,
    String orderImage,
    OrderStatus orderStatus,
    LocalDate date
) {}
