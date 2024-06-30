package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.dtos.EquipmentRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Image;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipmentMapper {
    public List<EquipmentListDto> entityListToDtoList(List<Equipment> equipments) {
        return equipments.stream().map(equipment -> new EquipmentListDto(
                equipment.getId(),
                equipment.getImages().get(1).getUrl(),
                equipment.getName(),
                equipment.getPrice(),
                equipment.getAuthor().getImageUrl(),
                getFullName(equipment),
                equipment.getDescription()
                )).collect(Collectors.toList());
    }

    public EquipmentDto entityToDto(Equipment equipment) {
        return new EquipmentDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getPrice(),
                equipment.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                equipment.getAuthor().getImageUrl(),
                getFullName(equipment),
                equipment.getDescription(),
                equipment.getContactInfo(),
                equipment.getQuantity()
        );
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

    public Equipment dtoToEntity(EquipmentRequestDto requestDto, List<Image> equipmentImages, AppUser user) {
        return new Equipment(
                requestDto.name(),
                requestDto.description(),
                requestDto.quantity(),
                requestDto.price(),
                requestDto.contactInfo(),
                equipmentImages,
                user
                );
    }
}





