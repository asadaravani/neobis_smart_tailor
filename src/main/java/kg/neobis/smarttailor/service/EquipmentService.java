package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.EquipmentDetailed;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EquipmentService {

    String addEquipment(String equipmentDto, List<MultipartFile> images, Authentication authentication);

    String buyEquipment(Long equipmentId, Authentication authentication);

    String deleteEquipment(Long equipmentId, Authentication authentication) throws IOException;

    Page<Equipment> findAllByUser(AppUser user, Pageable pageable);

    AdvertisementPageDto getAllEquipments(int pageNumber, int pageSize);

    EquipmentDetailed getEquipmentById(Long equipmentId);

    AdvertisementPageDto getUserEquipments(int pageNumber, int pageSize, Authentication authentication);

    String hideEquipment(Long equipmentId, Authentication authentication);

    AdvertisementPageDto searchEquipments(String query, int pageNumber, int pageSize);
}