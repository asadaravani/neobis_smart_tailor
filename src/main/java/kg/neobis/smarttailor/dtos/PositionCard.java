package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AccessRight;

import java.util.Set;

public record PositionCard(
    Long id,
    String position,
    int weight,
    Set<AccessRight> accessRights
) {}
