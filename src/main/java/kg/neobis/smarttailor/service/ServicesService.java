package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.ServiceDetailed;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Services;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServicesService {

    String addService(String serviceRequestDto, List<MultipartFile> images, Authentication authentication);

    String deleteService(Long serviceId, Authentication authentication) throws IOException;

    List<Services> findAllByUser(AppUser user);

    Services findServiceById(Long id);

    AdvertisementPageDto getAllServices(int pageNumber, int pageSize);

    ServiceDetailed getServiceById(Long id);

    String hideService(Long serviceId, Authentication authentication);
}