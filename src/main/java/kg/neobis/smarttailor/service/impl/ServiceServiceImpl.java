package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.ServiceAddRequest;
import kg.neobis.smarttailor.dtos.ServiceDetailedResponse;
import kg.neobis.smarttailor.dtos.ServicesPreviewResponse;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.exception.ResourceProcessingErrorException;
import kg.neobis.smarttailor.mapper.ServiceMapper;
import kg.neobis.smarttailor.repository.ServicesRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.ServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceServiceImpl implements ServicesService {
    ServicesRepository serviceRepository;
    AppUserService userService;
    CloudinaryService cloudinaryService;
    Validator validator;
    ObjectMapper objectMapper;

    @Override
    public Services findServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Service not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public ServiceDetailedResponse getServiceDetailed(Long id) {
        Services service = findServiceById(id);
        return ServiceMapper.INSTANCE.toDetailedResponse(service);
    }

    @Override
    public String addService(String requestDto, List<MultipartFile> files, String email) {
        try {
            ServiceAddRequest request = parseAndValidateServiceDto(requestDto);
            AppUser author = userService.findUserByEmail(email);
            Services newService = ServiceMapper.INSTANCE.toEntity(request, author);
            List<Image> uploadedImages = cloudinaryService.saveImages(files);
            newService.setImages(uploadedImages);
            serviceRepository.save(newService);
            return "Service successfully added";
        } catch (Exception e) {
            throw new ResourceProcessingErrorException("Error while adding a new Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ServicesPreviewResponse> getServices(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Services> services = serviceRepository.findAll(pageable);
        List<Services> servicesList = services.getContent();
        return servicesList.stream()
                .map(ServiceMapper.INSTANCE::toPreviewResponse).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public String deleteServiceById(Long id) {
        Services service = findServiceById(id);
        System.out.println("Deleting service with ID: " + id);
        service.getImages().forEach(image -> {
            try {
                System.out.println("Deleting image with URL: " + image.getUrl());
                cloudinaryService.deleteImage(image.getUrl());
            } catch (IOException e) {
                System.err.println("Error deleting image: " + image.getUrl() + " due to " + e.getMessage());
                throw new ResourceProcessingErrorException("Error with deleting images in Cloudinary", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
        serviceRepository.delete(service);
        System.out.println("Service deleted successfully");
        return "Service deleted";
    }

    private ServiceAddRequest parseAndValidateServiceDto(String dto) {
        try {
            ServiceAddRequest requestDto = objectMapper.readValue(dto, ServiceAddRequest.class);

            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "serviceAddRequest");
            validator.validate(dto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}