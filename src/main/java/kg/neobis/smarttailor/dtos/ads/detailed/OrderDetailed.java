package kg.neobis.smarttailor.dtos.ads.detailed;

import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderDetailed(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String contactInfo,
        String authorImage,
        String authorFullName,
        List<String> orderImages,
        LocalDate dateOfExecution,
        OrderStatus orderStatus,
        List<OrderItemDto> orderItems,
        List<OrganizationDto> orderCandidates
) implements Serializable {}