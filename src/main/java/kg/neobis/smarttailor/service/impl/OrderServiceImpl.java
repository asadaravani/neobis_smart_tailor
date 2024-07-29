package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.dtos.ads.list.OrderListDto;
import kg.neobis.smarttailor.dtos.ads.request.OrderRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.OrderStatus;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.repository.OrderRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    OrganizationEmployeeService organizationEmployeeService;
    Validator validator;

    @Override
    public String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication) {

        OrderRequestDto requestDto = parseAndValidateOrderRequestDto(orderRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        List<Image> orderImages = cloudinaryService.saveImages(images);

        Order order = orderMapper.dtoToEntity(requestDto, orderImages, user);
        orderRepository.save(order);
        return "Order has been created";
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId) throws IOException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        for (Image image : order.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }

        orderRepository.delete(order);
        return "Order has been deleted";
    }

    @Override
    public List<Order> findAllByUser(AppUser user) {
        return orderRepository.findAllByAuthor(user);
    }

    @Override
    public List<OrderListDto> getAllOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orders = orderRepository.findAll(pageable);
        List<Order> ordersList = orders.getContent();
        return orderMapper.entityListToDtoList(ordersList);
    }

    @Override
    public OrderDetailed getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.entityToDto(order);
    }

    @Override
    public String sendRequestToExecuteOrder(Long orderId, Authentication authentication) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (order.getAuthor().equals(user)) {
            throw new ResourceAlreadyExistsException("User can't respond to his/her own order");
        }

        if (order.getStatus() == OrderStatus.WAITING) {
            if (organizationEmployeeService.existsByEmployeeEmail(user.getEmail())) {
                if (organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.SEND_REQUEST_TO_EXECUTE_ORDER, user.getEmail())) {
                    if (!order.getCandidates().contains(user)) {
                        order.getCandidates().add(user);
                        orderRepository.save(order);
                    }
                    return "User has left a request to execute the order";
                } else {
                    throw new NoPermissionException("User has no permission to send request to execute order");
                }
            } else {
                throw new ResourceNotFoundException("User is not a member of any organization");
            }
        } else {
            throw new ResourceAlreadyExistsException("Order is already taken by another user");
        }
    }

    private OrderRequestDto parseAndValidateOrderRequestDto(String orderDto) {
        try {
            OrderRequestDto requestDto = objectMapper.readValue(orderDto, OrderRequestDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "orderDto");
            validator.validate(orderDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }
}