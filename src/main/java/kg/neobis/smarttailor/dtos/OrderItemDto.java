package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.Size;

import java.io.Serializable;

public record OrderItemDto(Size size, Integer quantity) implements Serializable {}