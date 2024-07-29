package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.ads.list.ServiceListDto;
import kg.neobis.smarttailor.dtos.ads.request.ServiceRequestDto;
import kg.neobis.smarttailor.dtos.ads.detailed.ServiceDetailed;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String addService(String serviceRequestDto, List<MultipartFile> images, Authentication authentication) {

        ServiceRequestDto requestDto = parseAndValidateServiceRequestDto(serviceRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        List<Image> serviceImages = cloudinaryService.saveImages(images);

        Services service = serviceMapper.dtoToEntity(requestDto, serviceImages, user);
        serviceRepository.save(service);
        return "Service has been created";
    }

    @Override
    @Transactional
    public String deleteService(Long serviceId) throws IOException {

        Services services = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        for (Image image : services.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }

        serviceRepository.delete(services);
        return "Service has been deleted";
    }

    @Override
    public List<ServiceListDto> getAllServices(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Services> services = serviceRepository.findAll(pageable);
        List<Services> servicesList = services.getContent();
        return serviceMapper.entityListToDtoList(servicesList);
    }

    @Override
    public ServiceDetailed getServiceById(Long serviceId) {
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        return serviceMapper.entityToDto(service);
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

    private ServiceRequestDto parseAndValidateServiceRequestDto(String serviceDto) {
        try {
            ServiceRequestDto requestDto = objectMapper.readValue(serviceDto, ServiceRequestDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "serviceDto");
            validator.validate(serviceDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }
}