package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.ads.detailed.EquipmentDetailed;
import kg.neobis.smarttailor.dtos.ads.list.EquipmentListDto;
import kg.neobis.smarttailor.dtos.ads.request.EquipmentRequestDto;
import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.enums.AdvertType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipmentMapper {

    public Equipment dtoToEntity(EquipmentRequestDto requestDto, List<Image> equipmentImages, AppUser user) {
        return new Equipment(
                requestDto.name(),
                requestDto.description(),
                requestDto.price(),
                requestDto.contactInfo(),
                requestDto.quantity(),
                equipmentImages,
                user
        );
    }

    public List<EquipmentListDto> entityListToDtoList(List<Equipment> equipments) {
        return equipments.stream().map(equipment -> new EquipmentListDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                getImageUrl(equipment.getImages(), 1),
                getFullName(equipment),
                getAuthorImageUrl(equipment)
        )).collect(Collectors.toList());
    }

    public EquipmentDetailed entityToDto(Equipment equipment) {
        return new EquipmentDetailed(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getContactInfo(),
                getAuthorImageUrl(equipment),
                getFullName(equipment),
                equipment.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                equipment.getQuantity()
        );
    }

    public MyAdvertisement toMyAdvertisement(Equipment equipment){
        return MyAdvertisement.builder()
                .id(equipment.getId())
                .imagePath(getImageUrl(equipment.getImages(), 0))
                .type(AdvertType.EQUIPMENT)
                .name(equipment.getName())
                .description(equipment.getDescription())
                .createdAt(equipment.getCreatedAt())
                .build();
    }

    private static String getAuthorImageUrl(Equipment equipment) {
        return (equipment.getAuthor() != null && equipment.getAuthor().getImage() != null) ?
                equipment.getAuthor().getImage().getUrl() : "";
    }

    private static String getFullName(Equipment equipment) {
        if (equipment == null || equipment.getAuthor() == null) {
            return "";
        }
        AppUser author = equipment.getAuthor();
        String name = author.getName() != null ? author.getName() : "";
        String surname = author.getSurname() != null ? author.getSurname() : "";
        String patronymic = author.getPatronymic() != null ? author.getPatronymic() : "";
        return (name + " " + surname + " " + patronymic).trim();
    }

    private static String getImageUrl(List<Image> images, int index) {
        return (images != null && images.size() > index) ? images.get(index).getUrl() : "";
    }
}