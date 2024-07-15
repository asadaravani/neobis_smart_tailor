package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.OrganizationMapper;
import kg.neobis.smarttailor.repository.OrganizationRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.OrganizationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationServiceImpl implements OrganizationService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    ObjectMapper objectMapper;
    OrganizationMapper organizationMapper;
    OrganizationRepository organizationRepository;
    Validator validator;

    @Override
    public String createOrganization(String organizationDto, MultipartFile organizationImage, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (!user.getHasSubscription())
            throw new ResourceNotFoundException("user has no subscription", HttpStatus.NOT_FOUND);

        if (organizationRepository.existsOrganizationByDirector(user))
            throw new ResourceAlreadyExistsException("user already has an organization", HttpStatus.CONFLICT);

        OrganizationDto requestDto = parseAndValidateOrganizationDto(organizationDto);
        if (organizationRepository.existsOrganizationByName(requestDto.name()))
            throw new ResourceAlreadyExistsException("organization with name \"".concat(requestDto.name().concat("\" already exists")), HttpStatus.CONFLICT);

        Image image = cloudinaryService.saveImage(organizationImage);

        Organization organization = organizationMapper.dtoToEntity(requestDto, image, user);
        organizationRepository.save(organization);
        return "organization has been created";
    }

    private OrganizationDto parseAndValidateOrganizationDto(String organizationDto) {
        try {
            OrganizationDto requestDto = objectMapper.readValue(organizationDto, OrganizationDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "orderDto");
            validator.validate(organizationDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}