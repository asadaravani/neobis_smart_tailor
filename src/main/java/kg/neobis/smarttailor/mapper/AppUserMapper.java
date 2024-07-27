package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "image.url", target = "imagePath")
    UserProfileDto toUserProfileDto(AppUser user);

    AppUser updateProfile(UserProfileEditRequest request, @MappingTarget AppUser user);
}