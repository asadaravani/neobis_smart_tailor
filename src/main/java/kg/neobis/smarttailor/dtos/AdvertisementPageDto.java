package kg.neobis.smarttailor.dtos;

import java.util.List;

public record AdvertisementPageDto(List<?> advertisement, boolean isLast, Long totalCount) {}