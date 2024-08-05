package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.ads.detailed.EquipmentDetailed;
import kg.neobis.smarttailor.dtos.ads.list.EquipmentListDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EquipmentService {

    String addEquipment(String equipmentDto, List<MultipartFile> images, Authentication authentication);

    String deleteEquipment(Long equipmentId) throws IOException;

    AdvertisementPageDto getAllEquipments(int pageNumber, int pageSize);

    EquipmentDetailed getEquipmentById(Long equipmentId);

    String hideEquipment(Long equipmentId, Authentication authentication);

    String buyEquipment(Long equipmentId, Authentication authentication);

    List<Equipment> findAllByUser(AppUser user);

    List<EquipmentListDto> searchEquipments(String query, Authentication authentication);
}