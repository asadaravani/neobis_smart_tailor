package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.entity.Position;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PositionService {

    String addPosition(String positionDto, Authentication authentication);

    List<PositionDto> getAllPositionsExceptDirector(Authentication authentication);

    List<PositionDto> getPositionsToInviteEmployee(Authentication authentication);

    List<Integer> getPositionsWithWeightsLessThan(Authentication authentication);

    Position getPositionByName(String name);

    Position save(Position position);
}