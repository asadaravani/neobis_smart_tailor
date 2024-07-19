package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ServiceAddRequest;
import kg.neobis.smarttailor.dtos.ServiceDetailedResponse;
import kg.neobis.smarttailor.dtos.ServicesPreviewResponse;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ServiceMapper {
    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "author.surname", target = "authorSurname")
    @Mapping(source = "author.patronymic", target = "patronymic")
    @Mapping(source = "author.image.url", target = "authorImagePath")
    @Mapping(source = "images", target = "imagePaths", qualifiedByName = "mapImagesToPaths")
    ServiceDetailedResponse toDetailedResponse(Services services);

    @Named("mapImagesToPaths")
    default List<String> mapImagesToPaths(List<Image> images){
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }


    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "author.surname", target = "authorSurname")
    @Mapping(source = "author.patronymic", target = "patronymic")
    @Mapping(source = "author.image.url", target = "authorImagePath")
    @Mapping(source = "images", target = "imagePath", qualifiedByName = "getFirstImagePath")
    ServicesPreviewResponse toPreviewResponse(Services service);

    @Named("getFirstImagePath")
    default String getFirstImagePath(List<Image> images){
        return images.stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse(null);
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactInfo", source = "request.contactInfo")
    @Mapping(target = "images", ignore = true)
    Services toEntity(ServiceAddRequest request, AppUser author);

    @Mapping(source = "images", target = "imagePath", qualifiedByName = "getFirstImagePath")
    @Mapping(target = "type", expression = "java(kg.neobis.smarttailor.enums.AdvertType.SERVICE)")
    MyAdvertisement toMyAdvertisement(Services service);

}
