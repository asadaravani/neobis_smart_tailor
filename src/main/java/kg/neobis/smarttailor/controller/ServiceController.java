package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.ServiceDetailedResponse;
import kg.neobis.smarttailor.dtos.ServicesPreviewResponse;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Service")
@RequestMapping(EndpointConstants.SERVICE_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceController {

    ServicesService servicesService;

    @GetMapping("/{id}")
    public ServiceDetailedResponse getServiceDetailed(@PathVariable Long id){
        return servicesService.getServiceDetailed(id);
    }

    @GetMapping
    public List<ServicesPreviewResponse> getAllServices(@RequestParam int pageNo,
                                                        @RequestParam int pageSize){
        return servicesService.getServices(pageNo, pageSize);
    }
    @PostMapping("/addService")
    public String addService(@RequestPart("dto") String dto,
                             @RequestPart("photos") List<MultipartFile> photos,
                             Authentication authentication){
        return servicesService.addService(dto, photos, authentication.getName());
    }
    @DeleteMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id){
        return servicesService.deleteServiceById(id);
    }

}
