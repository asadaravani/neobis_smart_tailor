package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.OrderDetailsDto;
import kg.neobis.smarttailor.dtos.OrderDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.repository.OrderRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.OrderService;
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
public class OrderServiceImpl implements OrderService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    ObjectMapper objectMapper;
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    Validator validator;

    @Override
    public String addOrder(String orderDto, List<MultipartFile> images, Authentication authentication) {

        OrderDto requestDto = parseAndValidateOrderDto(orderDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        cloudinaryService.validateImages(images);
        List<Image> orderImages = cloudinaryService.saveImages(images);

        Order order = orderMapper.dtoToEntity(requestDto, orderImages, user);
        orderRepository.save(order);
        return "order has been created";
    }

    @Override
    public OrderDetailsDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND.value()));
        return orderMapper.entityToOrderDetailsDto(order);
    }

    private OrderDto parseAndValidateOrderDto(String orderDto) {

        try {
            OrderDto requestDto = objectMapper.readValue(orderDto, OrderDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "orderDto");
            validator.validate(orderDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}