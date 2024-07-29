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
        return new Services(
                requestDto.name(),
                requestDto.description(),
                requestDto.price(),
                requestDto.contactInfo(),
                serviceImages,
                user
        );
    }

    public List<ServiceListDto> entityListToDtoList(List<Services> services) {
        return services.stream().map(service -> new ServiceListDto(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                getImageUrl(service.getImages(), 1),
                getFullName(service),
                getAuthorImageUrl(service)
        )).collect(Collectors.toList());
    }

    public ServiceDetailed entityToDto(Services services) {
        return new ServiceDetailed(
                services.getId(),
                services.getName(),
                services.getDescription(),
                services.getPrice(),
                services.getContactInfo(),
                getAuthorImageUrl(services),
                getFullName(services),
                services.getImages().stream().map(Image::getUrl).collect(Collectors.toList())
        );
    }

    public MyAdvertisement toMyAdvertisement(Services services) {
        return MyAdvertisement.builder()
                .id(services.getId())
                .type(AdvertType.SERVICE)
                .imagePath(getImageUrl(services.getImages(), 0))
                .name(services.getName())
                .description(services.getDescription())
                .createdAt(services.getCreatedAt())
                .build();
    }

    private static String getAuthorImageUrl(Services services) {
        return (services.getAuthor() != null && services.getAuthor().getImage() != null) ?
                services.getAuthor().getImage().getUrl() : "";
    }

    private static String getFullName(Services services) {
        if (services == null || services.getAuthor() == null) {
            return "";
        }
        AppUser author = services.getAuthor();
        String name = author.getName() != null ? author.getName() : "";
        String surname = author.getSurname() != null ? author.getSurname() : "";
        String patronymic = author.getPatronymic() != null ? author.getPatronymic() : "";
        return (name + " " + surname + " " + patronymic).trim();
    }

    private static String getImageUrl(List<Image> images, int index) {
        return (images != null && images.size() > index) ? images.get(index).getUrl() : "";
    }
}
