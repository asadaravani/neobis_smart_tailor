package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.entity.Equipment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipmentMapper {
    public List<EquipmentListDto> entityListToDtoList(List<Equipment> equipments) {
        return equipments.stream().map(equipment -> new EquipmentListDto(
                equipment.getImages().get(1).getUrl(),
                equipment.getName(),
                equipment.getPrice(),
                equipment.getAuthor().getImageUrl(),
                equipment.getAuthor().getName(),
                equipment.getDescription()
                )).collect(Collectors.toList());
    }
}
