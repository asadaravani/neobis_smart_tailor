package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Organization;

import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public Organization dtoToEntity(OrganizationDto dto, Image image, AppUser user) {
        return new Organization(
                image,
                dto.name(),
                dto.description(),
                user
        );
    }
}