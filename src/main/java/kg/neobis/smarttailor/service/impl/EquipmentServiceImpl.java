package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.dtos.EquipmentRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.repository.EquipmentRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentServiceImpl implements EquipmentService {

    AppUserService appUserService;
    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;
    ObjectMapper objectMapper;
    Validator validator;
    CloudinaryService cloudinaryService;

    @Override
    public List<EquipmentListDto> getAllEquipments() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentMapper.entityListToDtoList(equipmentList);
    }

    @Override
    public EquipmentDto getEquipmentById(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new ResourceNotFoundException("Equipment not found", HttpStatus.NOT_FOUND.value()));
        return equipmentMapper.entityToDto(equipment);
    }

    @Override
    public String addEquipment(String equipmentRequestDto, List<MultipartFile> images, Authentication authentication) {

        EquipmentRequestDto requestDto = parseAndValidateRecipeDto(equipmentRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        cloudinaryService.validateImages(images);
        List<Image> equipmentImages = cloudinaryService.saveImages(images);

        Equipment equipment = equipmentMapper.dtoToEntity(requestDto, equipmentImages, user);
        equipmentRepository.save(equipment);
        return "The equipment has been added successfully!";
    }

    private EquipmentRequestDto parseAndValidateRecipeDto(String equipmentDto) {
        try {
            EquipmentRequestDto requestDto = objectMapper.readValue(equipmentDto, EquipmentRequestDto.class);

            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "equipmentRequestDto");
            validator.validate(equipmentDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}