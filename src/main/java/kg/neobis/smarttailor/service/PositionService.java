package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.Position;
import org.springframework.security.core.Authentication;

public interface PositionService {

    String addPosition(String positionDto, Authentication authentication);

    Position getPositionByName(String name);

    Position save(Position position);
}