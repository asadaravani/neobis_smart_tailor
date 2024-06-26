package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    UserProfileDto appUserToUserProfileDto(AppUser request);
}