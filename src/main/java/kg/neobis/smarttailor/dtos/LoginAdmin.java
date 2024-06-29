package kg.neobis.smarttailor.dtos;

import lombok.Builder;

@Builder
public record LoginAdmin(
        String email,
        String password
) {}