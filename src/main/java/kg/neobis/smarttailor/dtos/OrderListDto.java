package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public record OrderListDto(Long orderId, String orderPhotoUrl, String name,
                           String orderDescription, Integer price,
                           LocalDate dateOfException) implements Serializable {}