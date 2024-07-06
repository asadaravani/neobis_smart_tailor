package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.ServiceDetailedResponse;
import kg.neobis.smarttailor.dtos.ServicesPreviewResponse;
import kg.neobis.smarttailor.entity.Services;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServicesService {
    Services findServiceById(Long id);

    ServiceDetailedResponse getServiceDetailed(Long id);

    String addService(String requestDto, List<MultipartFile> files, String email);

    List<ServicesPreviewResponse> getServices(int pageNo, int pageSize);

    String deleteServiceById(Long id);
}
