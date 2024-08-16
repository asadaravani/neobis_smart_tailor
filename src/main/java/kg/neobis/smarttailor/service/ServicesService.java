package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.AuthorServiceDetailedDto;
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

    List<Services> findUserServicePurchases(AppUser user);

    AdvertisementPageDto getAllVisibleServices(int pageNumber, int pageSize);

    ServiceDetailed getServiceDetailed(Long id);

    AuthorServiceDetailedDto getServiceDetailedForAuthor(Long serviceId, Authentication authentication);

    AdvertisementPageDto getUserServices(int pageNumber, int pageSize, Authentication authentication);

    String hideService(Long serviceId, Authentication authentication);

    String sendRequestToService(Long serviceId, Authentication authentication);
}