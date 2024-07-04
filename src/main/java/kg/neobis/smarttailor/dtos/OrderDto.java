package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record OrderDto(String name, String description, LocalDate dateOfExecution,
                       String contactInfo, Integer price, List<OrderItemDto> items) implements Serializable {}