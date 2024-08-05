package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.CurrentOrganizationOrders;
import kg.neobis.smarttailor.dtos.OrganizationOrders;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication);

    String assignOrderToOrganization(Long orderId, String organizationName, Authentication authentication);

    String deleteOrder(Long orderId, Authentication authentication) throws IOException;

    List<Order> findAllByUser(AppUser user);

    AdvertisementPageDto getAllOrders(int pageNumber, int pageSize);

    OrderDetailed getOrderById(Long orderId, Authentication authentication);

    String hideOrder(Long orderId, Authentication authentication);

    String sendRequestToExecuteOrder(Long orderId, Authentication authentication);

    List<OrganizationOrders> getOrdersOfOrganization(String email);

    CurrentOrganizationOrders getCurrentOrdersOfOrganization(String email);
}