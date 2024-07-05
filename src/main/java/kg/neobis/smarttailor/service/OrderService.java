package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.OrderDetailsDto;
import kg.neobis.smarttailor.dtos.OrderListDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {

    String addOrder(String orderDto, List<MultipartFile> images, Authentication authentication);

    List<OrderListDto> getAllOrders();

    OrderDetailsDto getOrderById(Long orderId);
}