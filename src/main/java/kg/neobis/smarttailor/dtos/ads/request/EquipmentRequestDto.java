package kg.neobis.smarttailor.dtos.ads.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;

public record EquipmentRequestDto(

        String name,
        String description,

        BigDecimal price,
        String contactInfo,

        Integer quantity
) implements Serializable {}