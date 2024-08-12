package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.enums.PlusMinus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication);

    String assignOrganizationToOrder(Long orderId, String organizationName, Authentication authentication);

    String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication);

    void changeOrderStatus(Long orderId, PlusMinus plusMinus, String email);

    String completeOrder(Long orderId, Authentication authentication);

    String deleteOrder(Long orderId, Authentication authentication) throws IOException;

    Page<Order> findAllByUser(AppUser user, Pageable pageable);

    List<Order> findAllByEmployee(AppUser employee);

    AdvertisementPageDto getAllOrders(int pageNumber, int pageSize);

    CurrentOrganizationOrders getCurrentOrdersOfOrganization(String email);

    EmployeePageDto getEmployeeOrdersByStage(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication);

    OrganizationPageDto getOrganizationOrdersByStage(String stage, int pageNumber, int pageSize, Authentication authentication);

    OrderDetailed getOrderById(Long orderId);

    AuthorOrderDetailedDto getOrderDetailedForAuthor(Long orderId, Authentication authentication);

    List<Long> getOrderIdsByEmployee(AppUser employee);

    AdvertisementPageDto getOrdersAssignedToUser(int pageNumber, int pageSize, Authentication authentication);

    List<OrganizationOrdersDto> getOrdersOfOrganization(String email);

    AdvertisementPageDto getUserOrders(int pageNumber, int pageSize, Authentication authentication);

    Order findOrderById(Long id);

    String hideOrder(Long orderId, Authentication authentication);

    String sendRequestToExecuteOrder(Long orderId, Authentication authentication);
}