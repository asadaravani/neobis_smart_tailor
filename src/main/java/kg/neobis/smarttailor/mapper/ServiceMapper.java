package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
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

    public AuthorServiceDetailedDto entityToAuthorServiceDetailedDto(Services service) {

        List<UserDto> applicants = service.getServiceApplicants().stream()
                .map(buyer -> new UserDto(
                        String.format("%s %s %s", buyer.getSurname(), buyer.getName(), buyer.getPatronymic()),
                        buyer.getEmail(),
                        buyer.getPhoneNumber()))
                .toList();

        return new AuthorServiceDetailedDto(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getContactInfo(),
                service.getAuthorImageUrl(service),
                service.getFullName(service),
                service.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                applicants
        );
    }

    public ServiceDetailed entityToServiceDetailed(Services service) {
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
