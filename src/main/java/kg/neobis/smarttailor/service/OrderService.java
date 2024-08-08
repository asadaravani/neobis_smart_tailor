package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication);

    String assignOrganizationToOrder(Long orderId, String organizationName, Authentication authentication);

    String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication);

    String deleteOrder(Long orderId, Authentication authentication) throws IOException;

    List<Order> findAllByUser(AppUser user);

    List<Order> findAllByEmployee(AppUser employee);

    AdvertisementPageDto getAllOrders(int pageNumber, int pageSize);

    List<EmployeeOrderListDto> getEmployeeCurrentOrders(Long employeeId, Authentication authentication);

    List<Long> getOrderIdsByEmployee(AppUser employee);

    OrderDetailed getOrderById(Long orderId);

    String hideOrder(Long orderId, Authentication authentication);

    String sendRequestToExecuteOrder(Long orderId, Authentication authentication);

    List<OrganizationOrders> getOrdersOfOrganization(String email);

    CurrentOrganizationOrders getCurrentOrdersOfOrganization(String email);
}