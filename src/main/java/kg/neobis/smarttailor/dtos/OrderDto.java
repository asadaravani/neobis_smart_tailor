package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.entity.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(String name, String description, List<Size> sizes,
                       LocalDateTime termOfExecution, String contactInfo, Integer price) implements Serializable {}