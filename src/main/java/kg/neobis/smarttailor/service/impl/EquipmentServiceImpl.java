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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentServiceImpl implements EquipmentService {

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
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(()-> new ResourceNotFoundException( "Equipment not found", HttpStatus.NOT_FOUND.value()));
        return equipmentMapper.entityToDto(equipment);
    }

    @Override
    public String addEquipment(String equipmentRequestDto, List<MultipartFile> images, Authentication authentication) {

        EquipmentRequestDto requestDto = parseAndValidateRecipeDto(equipmentRequestDto);
        AppUser user = getUserFromAuthentication(authentication);
        validateImages(images);
        List<Image> equipmentImages = saveImages(images);

        Equipment equipment = equipmentMapper.dtoToEntity(requestDto, equipmentImages, user);
        equipmentRepository.save(equipment);
        return "The equipment has been added successfully!";
    }

    private List<Image> saveImages(List<MultipartFile> images) {
        List<Image> imageList = new ArrayList<>();
        for(MultipartFile file: images){
            try {
                imageList.add(new Image(cloudinaryService.uploadImage(file)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return imageList;
    }

    private void validateImages(List<MultipartFile> images) {
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty() || image.getContentType() == null || !image.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("The file is not an image!");
            }
        }
    }




    private AppUser getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()){
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUser appUser){
                return appUser;
            }else {
                throw new IllegalArgumentException("Principal is not an instance of AppUser");
            }
        }
        return null;
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
