package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record OrderDetailsDto(List<String> orderImages, LocalDate dateOfExecution, Long id,
                              String name, String description, Integer price,
                              List<OrderItemDto> orderItems, String authorImage, String fullName,
                              String contactInfo) implements Serializable {}