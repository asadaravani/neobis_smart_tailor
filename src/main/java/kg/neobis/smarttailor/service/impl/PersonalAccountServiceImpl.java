package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.exception.ResourceProcessingErrorException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountServiceImpl implements PersonalAccountService {

    AppUserMapper appUserMapper;
    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    EquipmentMapper equipmentMapper;
    EquipmentService equipmentService;
    OrderMapper orderMapper;
    OrderService orderService;
    OrganizationEmployeeService organizationEmployeeService;
    ServiceMapper serviceMapper;
    ServicesService servicesService;

    @Override
    public String editProfile(UserProfileEditRequest request, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setPatronymic(request.patronymic());
        user.setPhoneNumber(request.phoneNumber());
        appUserService.save(user);

        return "User's data has been changed";
    }

    @Override
    public UserProfileDto getUserProfile(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Boolean inOrganization = organizationEmployeeService.existsByEmployeeEmail(user.getEmail());

        return appUserMapper.entityToDto(user, inOrganization);
    }

    @Override
    public String uploadProfileImage(MultipartFile file, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!user.getImage().getUrl().isEmpty()) {
            try {
                cloudinaryService.deleteImage(user.getImage().getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        user.getImage().setUrl(imageUrl);
        appUserService.save(user);

        return "Profile image has been uploaded";
    }

    @Override
    public List<MyAdvertisement> getUserAdvertisements(int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        try {
            List<MyAdvertisement> dto = new ArrayList<>();
            List<Services> services = servicesService.findAllByUser(user);
            List<Order> orders = orderService.findAllByUser(user);
            List<Equipment> equipments = equipmentService.findAllByUser(user);
            services.forEach(service -> dto.add(serviceMapper.toMyAdvertisement(service)));
            orders.forEach(order -> dto.add(orderMapper.toMyAdvertisement(order)));
            equipments.forEach(equipment -> dto.add(equipmentMapper.toMyAdvertisement(equipment)));
            return dto.stream()
                    .sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()))
                    .skip((long) pageNumber * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceProcessingErrorException("Error while returning resources");
        }
    }
}