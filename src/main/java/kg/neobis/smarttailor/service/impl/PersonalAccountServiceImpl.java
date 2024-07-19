package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.exception.ResourceProcessingErrorException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

    AppUserService userService;
    CloudinaryService cloudinaryService;
    EquipmentService equipmentService;
    ServicesService servicesService;
    OrderService orderService;
    OrderMapper orderMapper;
    EquipmentMapper equipmentMapper;

    @Override
    public UserProfileDto getUserProfile(String email) {
        AppUser user = userService.findUserByEmail(email);
        return AppUserMapper.INSTANCE.toUserProfileDto(user);
    }

    @Override
    public void uploadProfileImage(MultipartFile file, String email){
        AppUser user = userService.findUserByEmail(email);
        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!user.getImage().getUrl().isEmpty()){
            try {
                cloudinaryService.deleteImage(user.getImage().getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        user.getImage().setUrl(imageUrl);
        userService.save(user);
    }

    @Override
    public void editProfile(UserProfileEditRequest request, String email){
        AppUser updatedUser = AppUserMapper.INSTANCE.updateProfile(request, userService.findUserByEmail(email));
        userService.save(updatedUser);
    }

    @Override
    public List<MyAdvertisement> getUserAds(int pageNo, int pageSize, String email){
        try {
            List<MyAdvertisement> dto = new ArrayList<>();
            AppUser user = userService.findUserByEmail(email);
            List<Services> services = servicesService.findAllByUser(user);
            List<Order> orders = orderService.findAllByUser(user);
            List<Equipment> equipments = equipmentService.findAllByUser(user);
            services.forEach(service -> dto.add(ServiceMapper.INSTANCE.toMyAdvertisement(service)));
            orders.forEach(order -> dto.add(orderMapper.toMyAdvertisement(order)));
            equipments.forEach(equipment -> dto.add(equipmentMapper.toMyAdvertisement(equipment)));
            return dto.stream()
                    .sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()))
                    .skip((long) pageNo * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new ResourceProcessingErrorException("Error while returning resources", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}