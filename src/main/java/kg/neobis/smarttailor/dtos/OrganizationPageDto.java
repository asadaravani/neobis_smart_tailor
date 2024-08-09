package kg.neobis.smarttailor.dtos;

import java.util.List;

public record OrganizationPageDto(
        Long organizationId,
        String organizationName,
        String organizationDescription,
        List<?> orders,
        boolean isLast,
        Long totalCount
) {}