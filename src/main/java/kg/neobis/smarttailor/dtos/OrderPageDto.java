package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record OrderPageDto(
        List<?> orders, boolean isLast, Long totalCount

) implements Serializable {
}
