package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.AdvertisementListDto;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.dtos.UserProfileEditRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EquipmentService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.PersonalAccountService;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public AdvertisementPageDto getUserAdvertisements(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        List<Services> services = servicesService.findAllByUser(user);
        List<Order> orders = orderService.findAllByUser(user);
        List<Equipment> equipments = equipmentService.findAllByUser(user);

        List<MyAdvertisement> allAdvertisements = new ArrayList<>();
        services.forEach(service -> allAdvertisements.add(serviceMapper.toMyAdvertisement(service)));
        orders.forEach(order -> allAdvertisements.add(orderMapper.toMyAdvertisement(order)));
        equipments.forEach(equipment -> allAdvertisements.add(equipmentMapper.toMyAdvertisement(equipment)));

        allAdvertisements.sort((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()));

        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, allAdvertisements.size());

        List<MyAdvertisement> paginatedList;
        if (start >= allAdvertisements.size()) {
            paginatedList = new ArrayList<>();
        } else {
            paginatedList = allAdvertisements.subList(start, end);
        }

        boolean isLast = end >= allAdvertisements.size();
        long totalCount = allAdvertisements.size();

        return new AdvertisementPageDto(paginatedList, isLast, totalCount);
    }

    @Override
    public UserProfileDto getUserProfile(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Boolean inOrganization = organizationEmployeeService.existsByEmployeeEmail(user.getEmail());

        return appUserMapper.entityToUserProfileDto(user, inOrganization);
    }

    @Override
    public AdvertisementPageDto getUserPurchases(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        List<Services> servicesPage = servicesService.findUserServicePurchases(user);
        List<Order> ordersPage = orderService.findUserOrderPurchases(user);
        List<Equipment> equipmentPage = equipmentService.findUserEquipmentPurchases(user);

        List<AdvertisementListDto> allAdvertisements = new ArrayList<>();
        servicesPage.forEach(service -> allAdvertisements.add(serviceMapper.entityToAdvertisementListDto(service)));
        ordersPage.forEach(order -> allAdvertisements.add(orderMapper.entityToAdvertisementListDto(order)));
        equipmentPage.forEach(equipment -> allAdvertisements.add(equipmentMapper.entityToAdvertisementListDto(equipment)));

        allAdvertisements.sort((dto1, dto2) -> dto2.updatedAt().compareTo(dto1.updatedAt()));

        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, allAdvertisements.size());

        List<AdvertisementListDto> paginatedList;
        if (start >= allAdvertisements.size()) {
            paginatedList = new ArrayList<>();
        } else {
            paginatedList = allAdvertisements.subList(start, end);
        }

        boolean isLast = end >= allAdvertisements.size();
        long totalCount = allAdvertisements.size();

        return new AdvertisementPageDto(paginatedList, isLast, totalCount);
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
}