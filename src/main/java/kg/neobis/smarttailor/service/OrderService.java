package kg.neobis.smarttailor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.enums.PlusMinus;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    AdvertisementPageDto getOrganizationOrderHistory(int pageNumber, int pageSize, Authentication authentication);

    String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication) throws JsonProcessingException;

    OrganizationOrderHistoryDetailedDto getOrganizationOrderHistoryDetailed(Long orderId, Authentication authentication);

    String assignExecutorToOrder(Long orderId, Long userId, Authentication authentication);

    String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication);

    String changeOrderStatus(Long orderId, PlusMinus plusMinus, Authentication authentication);

    String completeOrder(Long orderId, Authentication authentication);

    String deleteOrder(Long orderId, Authentication authentication) throws IOException;

    AdvertisementPageDto getOrganizationOrderHistoryByEmployee(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication);

    List<Order> findAllByCandidate(AppUser user);

    List<Order> findAllByUser(AppUser user);

    List<Order> findCompletedUserOrders(AppUser user);

    List<Order> findCurrentUserOrders(AppUser user);

    String cancelOrder(Long orderId, Authentication authentication);

    Order findOrderById(Long id);

    List<Order> findUserOrderPurchases(AppUser user);

    AdvertisementPageDto getAllVisibleOrders(int pageNumber, int pageSize);

    CurrentOrderDetailed getCurrentOrderDetailed(Long orderId, Authentication authentication);

    CurrentOrganizationOrders getCurrentOrdersOfOrganization(Authentication authentication);

    EmployeePageDto getEmployeeOrdersByStage(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication);

    OrderDetailed getOrderDetailed(Long id);

    AuthorOrderDetailedDto getOrderDetailedForAuthor(Long orderId, Authentication authentication);

    List<EmployeeOrderListDto> getOrderInfoByEmployee(AppUser employee);

    List<OrganizationOrdersDto> getOrdersOfOrganization(String email);

    OrganizationPageDto getOrganizationOrdersByStage(String stage, int pageNumber, int pageSize, Authentication authentication);

    AdvertisementPageDto getUserOrderHistoryByStage(String stage, int pageNumber, int pageSize, Authentication authentication);

    AdvertisementPageDto getUserOrders(int pageNumber, int pageSize, Authentication authentication);

    String hideOrder(Long orderId, Authentication authentication);

    String sendRequestToExecuteOrder(Long orderId, Authentication authentication);


    AdvertisementPageDto searchOrders(String query, int pageNumber, int pageSize, Authentication authentication);
}