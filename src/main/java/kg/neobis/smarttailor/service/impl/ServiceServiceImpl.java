package kg.neobis.smarttailor.service.impl;

import com.cloudinary.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.ServiceDetailed;
import kg.neobis.smarttailor.dtos.AuthorServiceDetailedDto;
import kg.neobis.smarttailor.dtos.ads.service.ServiceRequestDto;
import kg.neobis.smarttailor.dtos.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ServiceListDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.exception.SelfPurchaseException;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.repository.ServicesRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceServiceImpl implements ServicesService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    ObjectMapper objectMapper;
    ServiceMapper serviceMapper;
    ServicesRepository serviceRepository;
    Validator validator;

    @Override
    @Transactional
    public String addService(String serviceRequestDto, List<MultipartFile> images, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        ServiceRequestDto requestDto = parseAndValidateServiceRequestDto(serviceRequestDto);
        List<Image> serviceImages = cloudinaryService.saveImages(images);

        Services service = serviceMapper.serviceRequestDtoToEntity(requestDto, serviceImages, user);

        serviceRepository.save(service);

        return "Service has been created";
    }

    @Override
    @Transactional
    public String deleteService(Long serviceId, Authentication authentication) throws IOException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!user.getId().equals(service.getAuthor().getId())) {
            throw new NoPermissionException("Only authors can delete their advertisements");
        }
        for (Image image : service.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }
        service.setServiceApplicants(null);
        serviceRepository.delete(service);

        return "Service has been deleted";
    }

    @Override
    public List<Services> findAllByUser(AppUser user) {
        return serviceRepository.findAllByAuthor(user);
    }

    @Override
    public Services findServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: ".concat(String.valueOf(id))));
    }

    @Override
    public List<Services> findUserServicePurchases(AppUser user) {
        return serviceRepository.findUserServicePurchases(user);
    }

    @Override
    public AdvertisementPageDto getAllVisibleServices(int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Services> services = serviceRepository.findByIsVisible(true, pageable);
        List<Services> servicesList = services.getContent();
        boolean isLast = services.isLast();
        Long totalCount = services.getTotalElements();

        List<ServiceListDto> serviceListDto = serviceMapper.entityListToDtoList(servicesList);

        return new AdvertisementPageDto(serviceListDto, isLast, totalCount);
    }

    @Override
    public ServiceDetailed getServiceDetailed(Long id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        return serviceMapper.entityToServiceDetailed(service);
    }

    @Override
    public AuthorServiceDetailedDto getServiceDetailedForAuthor(Long serviceId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Services service = serviceRepository.findById(serviceId).
                orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!service.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("User is not an author of this order");
        }

        return serviceMapper.entityToAuthorServiceDetailedDto(service);
    }

    @Override
    public AdvertisementPageDto getUserServices(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Services> services = serviceRepository.findAllByAuthor(user, pageable);
        List<MyAdvertisement> serviceList = new ArrayList<>();

        services.getContent().forEach(service -> serviceList.add(serviceMapper.toMyAdvertisement(service)));

        boolean isLast = services.isLast();
        Long totalCount = services.getTotalElements();

        return new AdvertisementPageDto(serviceList, isLast, totalCount);
    }

    @Override
    public String hideService(Long serviceId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!service.getIsVisible()) {
            throw new ResourceAlreadyExistsException("Service is already hidden");
        }
        if (!service.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("Only authors can hide their advertisements");
        }
        service.setIsVisible(false);
        serviceRepository.save(service);

        return "Service is now invisible in marketplace";
    }

    @Override
    public AdvertisementPageDto searchServices(String name, int pageNumber, int pageSize, Authentication authentication) {
        appUserService.getUserFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Services> services = serviceRepository.findServicesByNameContainingIgnoreCaseAndIsVisibleTrue(name, pageable);
        List<Services> servicesList = services.getContent();
        List<ServiceListDto> serviceListDto = serviceMapper.entityListToDtoList(servicesList);
        boolean isLast = services.isLast();
        Long totalCount = services.getTotalElements();
        return new AdvertisementPageDto(serviceListDto, isLast, totalCount);
    }

    @Override
    public String sendRequestToService(Long serviceId, Authentication authentication) {
        Services service = findServiceById(serviceId);
        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (service.getAuthor().getId().equals(user.getId())) {
            throw new SelfPurchaseException("User can't respond to his/her own advertisements");
        }
        if (service.getServiceApplicants().stream()
                .anyMatch(applicant -> applicant.getId().equals(user.getId()))) {
            throw new ResourceAlreadyExistsException("User has been sent the request to this service already");
        }
        service.getServiceApplicants().add(user);
        serviceRepository.save(service);

        return "User has left a request to service";
    }

    private ServiceRequestDto parseAndValidateServiceRequestDto(String serviceDto) {
        try {
            ServiceRequestDto requestDto = objectMapper.readValue(serviceDto, ServiceRequestDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "serviceDto");
            validator.validate(serviceDto, bindingResult);
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
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }
}