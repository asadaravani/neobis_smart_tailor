package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.enums.AccessRight;

import java.util.Set;

public record PositionDto(String positionName, Set<AccessRight> accessRights, Organization organization) {}