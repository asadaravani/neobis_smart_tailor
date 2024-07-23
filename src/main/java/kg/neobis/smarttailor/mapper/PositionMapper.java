package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.entity.Position;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public Position dtoToEntity(PositionDto dto) {
        return new Position(
                dto.positionName(),
                dto.accessRights()
        );
    }
}