package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.dtos.PositionsWeightGroups;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.PlusMinus;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

public interface PositionService {

    String addPosition(String positionDto, Authentication authentication);

    PositionsWeightGroups getPositionsByWeight(Authentication authentication);

    List<PositionDto> getAllPositionsExceptDirector(Authentication authentication);

    List<PositionDto> getAvailablePositionsForInvitation(Authentication authentication);

    String changePositionWeight(Long positionId, PlusMinus plusMinus, Authentication authentication);

    List<Integer> getPositionsWithWeightsLessThan(Authentication authentication);

    Set<AccessRight> getAvailableAccessRights(Authentication authentication);

    Position getPositionByName(String name);

    Position save(Position position);
}