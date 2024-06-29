package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.repository.EquipmentRepository;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentServiceImpl implements EquipmentService {

    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;

    @Override
    public List<EquipmentListDto> getAllEquipments() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentMapper.entityListToDtoList(equipmentList);
    }
}
