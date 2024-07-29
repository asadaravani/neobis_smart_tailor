package kg.neobis.smarttailor.dtos;

import java.io.Serializable;

public record OrderItemDto(String size, Integer quantity) implements Serializable {}