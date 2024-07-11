package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EquipmentService {

    List<EquipmentListDto> getAllEquipments();

    EquipmentDto getEquipmentById(Long equipmentId);

    String addEquipment(String equipmentDto, List<MultipartFile> images, Authentication authentication);

    String buyEquipment(Long equipmentId, Authentication authentication);
}