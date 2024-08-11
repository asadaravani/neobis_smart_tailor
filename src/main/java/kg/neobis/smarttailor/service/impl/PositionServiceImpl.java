package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.mapper.PositionMapper;
import kg.neobis.smarttailor.repository.PositionRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionServiceImpl implements PositionService {

    AppUserService appUserService;
    ObjectMapper objectMapper;
    OrganizationEmployeeService organizationEmployeeService;
    PositionMapper positionMapper;
    Validator validator;
    PositionRepository positionRepository;

    @Override
    public String addPosition(String positionDto, Authentication authentication) {

        PositionDto positionRequestDto = parseAndValidatePositionDto(positionDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.CREATE_POSITION, user.getEmail());

        if (hasRights) {
            if (positionRepository.existsPositionByNameAndOrganization(positionRequestDto.positionName(), organizationEmployee.getOrganization())) {
                throw new ResourceAlreadyExistsException("Position with name '".concat(positionRequestDto.positionName()).concat("' already exists"));
            }
            Position position = positionMapper.dtoToEntity(positionRequestDto, organizationEmployee.getOrganization());
            positionRepository.save(position);
        } else {
            throw new NoPermissionException("User has no permission to create position");
        }
        return "Position has been created";
    }

    @Override
    @Cacheable(value = "allPositions", key = "#authentication.name")
    public List<PositionDto> getAllPositionsExceptDirector(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        List<Position> positions = positionRepository.findAllPositionsExceptDirector(organizationEmployee.getOrganization());

        return positionMapper.entityListToDtoList(positions);
    }

    @Override
    public Position getPositionByName(String name) {
        return positionRepository.getByName(name);
    }

    @Override
    public Position save(Position position) {
        return positionRepository.save(position);
    }

    private PositionDto parseAndValidatePositionDto(String positionDto) {
        try {
            PositionDto requestDto = objectMapper.readValue(positionDto, PositionDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "positionDto");
            validator.validate(positionDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }
}