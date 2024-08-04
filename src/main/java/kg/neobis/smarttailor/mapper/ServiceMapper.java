package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ads.detailed.ServiceDetailed;
import kg.neobis.smarttailor.dtos.ads.list.ServiceListDto;
import kg.neobis.smarttailor.dtos.ads.request.ServiceRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.enums.AdvertType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceMapper {

    public Services dtoToEntity(ServiceRequestDto requestDto, List<Image> serviceImages, AppUser user) {
        return Services.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .price(requestDto.price())
                .contactInfo(requestDto.contactInfo())
                .isVisible(true)
                .author(user)
                .images(serviceImages)
                .build();
    }

    public List<ServiceListDto> entityListToDtoList(List<Services> services) {
        return services.stream().map(service -> new ServiceListDto(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getFirstImage(service.getImages()),
                service.getFullName(service),
                service.getAuthorImageUrl(service)
        )).collect(Collectors.toList());
    }

    public ServiceDetailed entityToDto(Services service) {
        return new ServiceDetailed(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getContactInfo(),
                service.getAuthorImageUrl(service),
                service.getFullName(service),
                service.getImages().stream().map(Image::getUrl).collect(Collectors.toList())
        );
    }

    public MyAdvertisement toMyAdvertisement(Services service) {
        return MyAdvertisement.builder()
                .id(service.getId())
                .type(AdvertType.SERVICE)
                .imagePath(service.getFirstImage(service.getImages()))
                .name(service.getName())
                .description(service.getDescription())
                .createdAt(service.getCreatedAt())
                .build();
    }
}
