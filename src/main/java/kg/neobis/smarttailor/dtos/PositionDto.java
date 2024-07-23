package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AccessRight;

import java.util.List;

public record PositionDto(String positionName, List<AccessRight> accessRights) {}