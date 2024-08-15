package kg.neobis.smarttailor.service.impl;

import com.cloudinary.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.AuthorEquipmentDetailedDto;
import kg.neobis.smarttailor.dtos.EquipmentDetailed;
import kg.neobis.smarttailor.dtos.EquipmentRequestDto;
import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.dtos.NotificationPdfDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.OutOfStockException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.exception.SelfPurchaseException;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.repository.EquipmentRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EquipmentService;
import kg.neobis.smarttailor.service.NotificationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentServiceImpl implements EquipmentService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    EquipmentMapper equipmentMapper;
    EquipmentRepository equipmentRepository;
    NotificationService notificationService;
    ObjectMapper objectMapper;
    Validator validator;

    @Override
    @Transactional
    public String addEquipment(String equipmentRequestDto, List<MultipartFile> images, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        EquipmentRequestDto requestDto = parseAndValidateEquipmentRequestDto(equipmentRequestDto);
        List<Image> equipmentImages = cloudinaryService.saveImages(images);

        Equipment equipment = equipmentMapper.dtoToEntity(requestDto, equipmentImages, user);

        equipmentRepository.save(equipment);

        return "Equipment has been created";
    }

    @Override
    @Transactional
    public String buyEquipment(Long equipmentId, Authentication authentication) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        AppUser user = appUserService.getUserFromAuthentication(authentication);

        validatePurchase(equipment, user);

        updateEquipmentStock(equipment);

        if (!equipment.getEquipmentBuyers().contains(user)) {
            equipment.getEquipmentBuyers().add(user);
        }
        equipmentRepository.save(equipment);

        notificationService.sendNotification(
                new NotificationDto("Equipment has been sold!", equipment.getName() + " has been bought by user " + user.getName(), LocalDateTime.now()),
                new NotificationPdfDto(user.getName() + " " + user.getSurname(), user.getEmail(), equipment.getName(), equipment.getPrice())
        );

        return "You have successfully purchased the equipment. Receipt sent to the email. Please check your email";
    }

    @Override
    @Transactional
    public String deleteEquipment(Long equipmentId, Authentication authentication) throws IOException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        if (!user.getId().equals(equipment.getAuthor().getId())) {
            throw new NoPermissionException("Only authors can delete their advertisements");
        }
        for (Image image : equipment.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }
        equipment.setEquipmentBuyers(null);
        equipmentRepository.delete(equipment);

        return "Equipment has been deleted";
    }

    @Override
    public Page<Equipment> findAllByUser(AppUser user, Pageable pageable) {
        return equipmentRepository.findAllByAuthor(user, pageable);
    }

    @Override
    public AdvertisementPageDto getAllVisibleEquipments(int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Equipment> equipments = equipmentRepository.findByIsVisibleAndQuantityGreaterThan(true, 0, pageable);
        List<Equipment> equipmentList = equipments.getContent();
        boolean isLast = equipments.isLast();
        Long totalCount = equipments.getTotalElements();

        List<EquipmentListDto> equipmentListDto = equipmentMapper.entityListToDtoList(equipmentList);

        return new AdvertisementPageDto(equipmentListDto, isLast, totalCount);
    }

    @Override
    public EquipmentDetailed getEquipmentDetailed(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        return equipmentMapper.entityToEquipmentDetailed(equipment);
    }

    @Override
    public AuthorEquipmentDetailedDto getEquipmentDetailedForAuthor(Long equipmentId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Equipment equipment = equipmentRepository.findById(equipmentId).
                orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        if (!equipment.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("User is not an author of this advertisement");
        }

        return equipmentMapper.entityToAuthorEquipmentDetailedDto(equipment);
    }

    @Override
    public AdvertisementPageDto getUserEquipments(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Equipment> equipments = equipmentRepository.findAllByAuthor(user, pageable);
        List<MyAdvertisement> equipmentList = new ArrayList<>();

        equipments.getContent().forEach(service -> equipmentList.add(equipmentMapper.toMyAdvertisement(service)));

        boolean isLast = equipments.isLast();
        Long totalCount = equipments.getTotalElements();

        return new AdvertisementPageDto(equipmentList, isLast, totalCount);
    }

    @Override
    public String hideEquipment(Long equipmentId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        if (!equipment.getIsVisible()) {
            throw new ResourceAlreadyExistsException("Equipment is already hidden");
        }
        if (!equipment.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("Only authors can hide their advertisements");
        }
        equipment.setIsVisible(false);
        equipmentRepository.save(equipment);

        return "Equipment is now invisible in marketplace";
    }

    @Override
    public AdvertisementPageDto searchEquipments(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Equipment> equipments = equipmentRepository.findEquipmentByNameContainingIgnoreCase(name, pageable);
        List<Equipment> equipmentList = equipments.getContent();
        List<EquipmentListDto> equipmentListDto = equipmentMapper.entityListToDtoList(equipmentList);
        boolean isLast = equipments.isLast();
        Long totalCount = equipments.getTotalElements();
        return new AdvertisementPageDto(equipmentListDto, isLast, totalCount);
    }

    private boolean isOutOfStock(Equipment equipment) {
        return equipment.getQuantity() == null || equipment.getQuantity() <= 0;
    }

    private boolean isOwner(Equipment equipment, AppUser user) {
        return equipment.getAuthor().getId().equals(user.getId());
    }

    private EquipmentRequestDto parseAndValidateEquipmentRequestDto(String equipmentDto) {
        try {
            EquipmentRequestDto requestDto = objectMapper.readValue(equipmentDto, EquipmentRequestDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "equipmentDto");
            validator.validate(equipmentDto, bindingResult);

            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            if (StringUtils.isBlank(requestDto.name())) {
                throw new InvalidRequestException("Name cannot be empty");
            } else if (requestDto.name().length() < 5 || requestDto.name().length() > 50) {
                throw new InvalidRequestException("Name size must be between 2 and 50");
            }
            if (StringUtils.isBlank(requestDto.description())) {
                throw new InvalidRequestException("Description cannot be empty");
            } else if (requestDto.description().length() < 5 || requestDto.description().length() > 1000) {
                throw new InvalidRequestException("Description size must be between 2 and 1000");
            }
            if (requestDto.price() == null || requestDto.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidRequestException("Price must be greater than zero");
            }
            if (StringUtils.isBlank(requestDto.contactInfo())) {
                throw new InvalidRequestException("Contact info cannot be empty");
            } else if (requestDto.contactInfo().length() > 320) {
                throw new InvalidRequestException("Contact info's size cannot be greater than 320");
            }
            if (requestDto.quantity() < 0) {
                throw new InvalidRequestException("Quantity must be greater than zero");
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    private void updateEquipmentStock(Equipment equipment) {
        equipment.setQuantity(equipment.getQuantity() - 1);
        equipmentRepository.save(equipment);
    }

    private void validatePurchase(Equipment equipment, AppUser user) {
        if (isOwner(equipment, user)) {
            throw new SelfPurchaseException("Users can't buy their own equipment");
        }
        if (isOutOfStock(equipment)) {
            throw new OutOfStockException("This equipment is out of stock");
        }
    }

}