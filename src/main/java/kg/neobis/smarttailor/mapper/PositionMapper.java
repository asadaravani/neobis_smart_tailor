package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PositionMapper {

    public Position dtoToEntity(PositionDto dto, Organization organization) {
        return new Position(
                dto.positionName(),
                dto.weight(),
                dto.accessRights(),
                organization
        );
    }

    public List<PositionDto> entityListToDtoList(List<Position> positions) {

        return positions.stream().map(position -> new PositionDto(
                position.getName(),
                position.getWeight(),
                position.getAccessRights()
        )).collect(Collectors.toList());
    }
}