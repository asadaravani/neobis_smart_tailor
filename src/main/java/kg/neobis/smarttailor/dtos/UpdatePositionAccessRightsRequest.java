package kg.neobis.smarttailor.dtos;

import kg.neobis.smarttailor.enums.AccessRight;

import java.util.Set;

public record UpdatePositionAccessRightsRequest(
        Set<AccessRight> accessRights
) {}