package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
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
        return Equipment.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .price(requestDto.price())
                .contactInfo(requestDto.contactInfo())
                .isVisible(true)
                .author(user)
                .quantity(requestDto.quantity())
                .images(equipmentImages)
                .build();
    }

    public List<EquipmentListDto> entityListToDtoList(List<Equipment> equipments) {
        return equipments.stream().map(equipment -> new EquipmentListDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getFirstImage(equipment.getImages()),
                equipment.getFullName(equipment),
                equipment.getAuthorImageUrl(equipment)
        )).collect(Collectors.toList());
    }

    public AuthorEquipmentDetailedDto entityToAuthorEquipmentDetailedDto(Equipment equipment) {

        List<UserDto> buyers = equipment.getEquipmentBuyers().stream()
                .map(buyer -> new UserDto(
                        buyer.getSurname().concat(" ").concat(buyer.getName()),
                        buyer.getEmail(),
                        buyer.getPhoneNumber()))
                .toList();

        return new AuthorEquipmentDetailedDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getContactInfo(),
                equipment.getAuthorImageUrl(equipment),
                equipment.getFullName(equipment),
                equipment.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                buyers
        );
    }

    public EquipmentDetailed entityToDto(Equipment equipment) {
        return new EquipmentDetailed(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getContactInfo(),
                equipment.getAuthorImageUrl(equipment),
                equipment.getFullName(equipment),
                equipment.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                equipment.getQuantity()
        );
    }

    public MyAdvertisement toMyAdvertisement(Equipment equipment) {
        return MyAdvertisement.builder()
                .id(equipment.getId())
                .imagePath(equipment.getFirstImage(equipment.getImages()))
                .type(AdvertType.EQUIPMENT)
                .name(equipment.getName())
                .description(equipment.getDescription())
                .createdAt(equipment.getCreatedAt())
                .build();
    }
}