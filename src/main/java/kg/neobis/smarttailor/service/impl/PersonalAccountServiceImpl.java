package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Cacheable(value = "userAds", key = "#authentication.name + '_' + #pageNumber + '_' + #pageSize")
    public AdvertisementPageDto getUserAdvertisements(int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Services> servicesPage = servicesService.findAllByUser(user, pageable);
        Page<Order> ordersPage = orderService.findAllByUser(user, pageable);
        Page<Equipment> equipmentPage = equipmentService.findAllByUser(user, pageable);

        List<MyAdvertisement> allAdvertisements = new ArrayList<>();
        servicesPage.getContent().forEach(service -> allAdvertisements.add(serviceMapper.toMyAdvertisement(service)));
        ordersPage.getContent().forEach(order -> allAdvertisements.add(orderMapper.toMyAdvertisement(order)));
        equipmentPage.getContent().forEach(equipment -> allAdvertisements.add(equipmentMapper.toMyAdvertisement(equipment)));

        allAdvertisements.sort((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allAdvertisements.size());
        List<MyAdvertisement> paginatedList = allAdvertisements.subList(start, end);

        boolean isLast = end >= allAdvertisements.size();
        long totalCount = allAdvertisements.size();

        return new AdvertisementPageDto(paginatedList, isLast, totalCount);
    }

    @Override
    @Cacheable(value = "userProfile", key = "#authentication.name")
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
}