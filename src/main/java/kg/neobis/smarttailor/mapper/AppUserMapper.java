package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "user.image.url", target = "imagePath")
    UserProfileDto toUserProfileDto(AppUser user);
}