package kg.neobis.smarttailor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication) throws JsonProcessingException;

    String assignOrganizationToOrder(Long orderId, String organizationName, Authentication authentication);

    String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication);

    String changeOrderStatus(Long orderId, PlusMinus plusMinus, Authentication authentication);

    String completeOrder(Long orderId, Authentication authentication);

    String deleteOrder(Long orderId, Authentication authentication) throws IOException;

    List<Order> findAllByEmployee(AppUser employee);

    Page<Order> findAllByUser(AppUser user, Pageable pageable);

    Order findOrderById(Long id);

    AdvertisementPageDto getAllVisibleOrders(int pageNumber, int pageSize);

    CurrentOrganizationOrders getCurrentOrdersOfOrganization(String email);

    EmployeePageDto getEmployeeOrdersByStage(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication);

    OrderDetailed getOrderDetailed(Long id);

    AuthorOrderDetailedDto getOrderDetailedForAuthor(Long orderId, Authentication authentication);

    List<EmployeeOrderListDto> getOrderInfoByEmployee(AppUser employee);

    AdvertisementPageDto getOrdersAssignedToUser(int pageNumber, int pageSize, Authentication authentication);

    List<OrganizationOrdersDto> getOrdersOfOrganization(String email);

    OrganizationPageDto getOrganizationOrdersByStage(String stage, int pageNumber, int pageSize, Authentication authentication);

    AdvertisementPageDto getUserOrderHistoryByStage(String stage, int pageNumber, int pageSize, Authentication authentication);

    AdvertisementPageDto getUserOrders(int pageNumber, int pageSize, Authentication authentication);

    String hideOrder(Long orderId, Authentication authentication);

    String sendRequestToExecuteOrder(Long orderId, Authentication authentication);
}