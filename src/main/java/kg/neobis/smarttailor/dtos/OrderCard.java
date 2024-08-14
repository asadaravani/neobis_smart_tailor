package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public record OrderCard(
        Long id,
        String name,
        String description,
        LocalDate dateOfStart
) implements Serializable {}