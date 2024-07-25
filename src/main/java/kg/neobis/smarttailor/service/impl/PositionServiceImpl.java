package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.mapper.PositionMapper;
import kg.neobis.smarttailor.repository.PositionRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

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

        PositionDto requestDto = parseAndValidatePositionDto(positionDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Boolean hasRights = organizationEmployeeService.existsByPositionNameAndEmployeeEmail(AccessRight.CREATE_POSITION.name(), user.getEmail());

        if (user.getHasSubscription() || hasRights) {
            if (positionRepository.existsPositionByName(requestDto.positionName())) {
                throw new ResourceAlreadyExistsException("Position with name \"".concat(requestDto.positionName()).concat("\" already exists"));
            }
            Position position = positionMapper.dtoToEntity(requestDto);
            positionRepository.save(position);
        }
        return "position has been created";
    }

    @Override
    public Position getPositionByName(String name) {
        return positionRepository.getByName(name);
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