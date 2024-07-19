package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.OrderDetailsDto;
import kg.neobis.smarttailor.dtos.OrderListDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String addOrder(String orderDto, List<MultipartFile> images, Authentication authentication);

    String deleteOrder(Long orderId) throws IOException;

    List<OrderListDto> getAllOrders();

    OrderDetailsDto getOrderById(Long orderId);

    List<Order> findAllByUser(AppUser user);
}