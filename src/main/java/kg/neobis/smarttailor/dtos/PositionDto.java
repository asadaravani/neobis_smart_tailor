package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AccessRight;

import java.io.Serializable;
import java.util.Set;

public record PositionDto(String positionName, Set<AccessRight> accessRights) implements Serializable {}