package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.EquipmentListDto;

import java.util.List;
import java.util.Optional;

public interface EquipmentService {
    List<EquipmentListDto> getAllEquipments();

}
