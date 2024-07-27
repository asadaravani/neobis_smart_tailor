package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrganizationDetailed;
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

    public OrganizationDetailed toOrganizationDetailed(Organization organization) {
        return OrganizationDetailed.builder()
                .id(organization.getId())
                .imagePath(getImageUrl(organization.getImage()))
                .name(organization.getName())
                .description(organization.getDescription())
                .createdAt(organization.getCreatedAt())
                .build();
    }

    private static String getImageUrl(Image image) {
        return (image != null) ? image.getUrl() : "";
    }
}