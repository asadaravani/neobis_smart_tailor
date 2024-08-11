package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.dtos.OrderDetailed;
import kg.neobis.smarttailor.dtos.OrderListDto;
import kg.neobis.smarttailor.dtos.OrderRequestDto;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.OrderStatus;
import kg.neobis.smarttailor.enums.PlusMinus;
import kg.neobis.smarttailor.exception.*;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.repository.OrderRepository;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    ObjectMapper objectMapper;
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    OrganizationService organizationService;
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
    public String assignOrganizationToOrder(Long orderId, String organizationName, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);
        Organization organization = organizationService.getOrganizationByName(organizationName);

        if (!order.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("User cannot manage an order that is not his own");
        }
        if (order.getOrganizationCandidates().stream().noneMatch(org -> org.getId().equals(organization.getId()))) {
            throw new ResourceNotFoundException("Organization \"".concat(organizationName).concat("\" hasn't sent request"));
        }
        order.setOrganizationExecutor(organization);
        order.setDateOfStart(LocalDate.now());
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        return "Order has been assigned to \"".concat(organizationName).concat("\" organization");
    }

    @Override
    public String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);
        AppUser employee = appUserService.findUserById(employeeId);

        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.ASSIGN_EMPLOYEE_TO_ORDER, user.getEmail());
        OrganizationEmployee authenticatedOrganizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("Authenticated user is not a member of any organization"));
        OrganizationEmployee assignedToOrderEmployee = organizationEmployeeService.findByEmployeeEmail(employee.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("Employee is not a member of any organization"));

        if (hasRights) {
            if (authenticatedOrganizationEmployee.getOrganization().getId()
                    .equals(assignedToOrderEmployee.getOrganization().getId())) {
                if (order.getDateOfCompletion() == null) {
                    if (!order.getOrderEmployees().contains(employee)) {
                        order.getOrderEmployees().add(employee);
                        orderRepository.save(order);

                        return "Employee has been assigned to order";
                    } else {
                        throw new ResourceAlreadyExistsException("Employee is already assigned to order");
                    }
                } else {
                    throw new OutOfDateException("Order is already completed");
                }
            } else {
                throw new ResourceNotFoundException("User can't assign order to employee, who is not a member of organization");
            }
        } else {
            throw new NoPermissionException("User has no permission to assign employee to order");
        }
    }

    @Override
    public void changeOrderStatus(Long orderId, PlusMinus plusMinus, String email) {
        AppUser user = appUserService.findUserByEmail(email);
        Order order = findOrderById(orderId);
        OrderStatus[] statusArray = OrderStatus.values();
        Organization organization = organizationService.findOrganizationByDirectorOrEmployee(email);
        if (!order.getOrganizationExecutor().getId().equals(organization.getId()) || !order.getOrderEmployees().contains(user)) {
            throw new NoPermissionException("You do NOT have permission to change the status of this Order");
        }
        if ((plusMinus == PlusMinus.MINUS && order.getStatus() == OrderStatus.WAITING)
                || (plusMinus == PlusMinus.PLUS && order.getStatus() == OrderStatus.ARRIVED)) {
            throw new InvalidRequestException("Invalid Request");
        }
        Arrays.stream(statusArray).forEach(status -> {
            if (status == order.getStatus()) {
                if (plusMinus == PlusMinus.PLUS) {
                    order.setStatus(statusArray[Arrays.asList(statusArray).indexOf(status) + 1]);
                } else {
                    order.setStatus(statusArray[Arrays.asList(statusArray).indexOf(status) - 1]);
                }
            }
        });
        orderRepository.save(order);
    }

    @Override
    public String completeOrder(Long orderId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);

        if (order.getOrganizationExecutor() == null) {
            throw new ResourceNotFoundException("Customer hasn't chosen an executor to order");
        }
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.COMPLETE_ORDER, user.getEmail());
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));

        if (hasRights) {
            if (organizationEmployee.getOrganization().getId().
                    equals(order.getOrganizationExecutor().getId())) {
                if (order.getDateOfCompletion() == null) {
                    order.setDateOfCompletion(LocalDate.now());
                    orderRepository.save(order);

                    return "Order has been completed";
                } else {
                    throw new ResourceAlreadyExistsException("Order is already completed");
                }
            } else {
                throw new NoPermissionException("Order taken by another organization");
            }
        } else {
            throw new NoPermissionException("User has no permission to complete order");
        }
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId, Authentication authentication) throws IOException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);

        if (!user.getId().equals(order.getAuthor().getId())) {
            throw new NoPermissionException("Only authors can delete their advertisements");
        }
        for (Image image : order.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }
        orderRepository.delete(order);

        return "Order has been deleted";
    }

    @Override
    public Page<Order> findAllByUser(AppUser user, Pageable pageable) {
        return orderRepository.findAllByAuthor(user, pageable);
    }

    @Override
    public List<Order> findAllByEmployee(AppUser employee) {
        return orderRepository.findByOrderEmployee(employee);
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public AdvertisementPageDto getAllOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByIsVisible(true, pageable);
        List<Order> ordersList = orders.getContent();
        List<OrderListDto> orderListDto = orderMapper.entityListToDtoList(ordersList);
        boolean isLast = orders.isLast();
        Long totalCount = orders.getTotalElements();
        return new AdvertisementPageDto(orderListDto, isLast, totalCount);
    }

    @Override
    public CurrentOrganizationOrders getCurrentOrdersOfOrganization(String email) {
        Organization organization = organizationService.findOrganizationByDirectorOrEmployee(email);
        List<Order> orders = orderRepository.findAllByOrganizationExecutor(organization);
        return new CurrentOrganizationOrders(
                extractOrdersByStatusAndMap(OrderStatus.WAITING, orders),
                extractOrdersByStatusAndMap(OrderStatus.IN_PROGRESS, orders),
                extractOrdersByStatusAndMap(OrderStatus.CHECKING, orders),
                extractOrdersByStatusAndMap(OrderStatus.SENDING, orders),
                extractOrdersByStatusAndMap(OrderStatus.ARRIVED, orders)
        );
    }

    @Override
    public EmployeePageDto getEmployeeOrdersByStage(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee authenticatedOrganizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("Authenticated user is not a member of any organization"));
        Organization organization = authenticatedOrganizationEmployee.getOrganization();
        AppUser employee = appUserService.findUserById(employeeId);

        Boolean isEmployeeInOrganization = organizationEmployeeService.existsByOrganizationAndEmployeeEmail(organization, employee.getEmail());
        if (!isEmployeeInOrganization) {
            throw new UserNotInOrganizationException("Employee is not a member of authenticated user organization");
        }
        Pageable pageable;
        Page<Order> employeeOrders;
        if (stage.equals("current")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfStart"));
            employeeOrders = orderRepository.findCurrentEmployeeOrders(employee, pageable);
        } else if (stage.equals("completed")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfCompletion"));
            employeeOrders = orderRepository.findCompletedEmployeeOrders(employee, pageable);
        } else {
            throw new ResourceNotFoundException("Invalid stage.\nValid states: completed, current");
        }
        List<EmployeeStageOrderListDto> orderListDto = orderMapper.entityListToEmployeeStageOrderListDto(employeeOrders, stage);
        boolean isLast = employeeOrders.isLast();
        Long totalCount = employeeOrders.getTotalElements();
        String employeeFullName = employee.getSurname().concat(" ")
                .concat(employee.getName()).concat(" ")
                .concat(employee.getPatronymic());
        return new EmployeePageDto(employee.getId(), employeeFullName, orderListDto, isLast, totalCount);
    }

    @Override
    public OrganizationPageDto getOrganizationOrdersByStage(String stage, int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        Organization organization = organizationEmployee.getOrganization();

        Page<Order> organizationOrders;
        if (stage.equals("current")) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "dateOfStart");
            organizationOrders = orderRepository.findCurrentOrganizationOrders(organization, pageable);
        } else if (stage.equals("completed")) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "dateOfCompletion");
            organizationOrders = orderRepository.findCompletedOrganizationOrders(organization, pageable);
        } else {
            throw new ResourceNotFoundException("Invalid state.\nValid states: completed, current");
        }
        List<Order> ordersList = organizationOrders.getContent();
        List<OrganizationOrdersDto> orderListDto = orderMapper.entityListToOrganizationOrderListDto(ordersList);
        boolean isLast = organizationOrders.isLast();
        Long totalCount = organizationOrders.getTotalElements();
        return new OrganizationPageDto(organization.getId(), organization.getName(),
                                        organization.getDescription(),orderListDto, isLast, totalCount);
    }

    @Override
    public OrderDetailed getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        return orderMapper.entityToDto(order);
    }

    @Override
    public AuthorOrderDetailedDto getOrderDetailedForAuthor(Long orderId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("User is not an author of this order");
        }
        return orderMapper.entityToAuthorOrderDetailedDto(order);
    }

    @Override
    public List<Long> getOrderIdsByEmployee(AppUser employee) {
        return findAllByEmployee(employee)
                .stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationOrdersDto> getOrdersOfOrganization(String email) {
        Organization organization = organizationService.findOrganizationByDirectorOrEmployee(email);
        List<Order> orders = orderRepository.findAllByOrganizationExecutor(organization);
        return orders.stream().map(
                orderMapper::toOrganizationOrders
        ).toList();
    }

    @Override
    public AdvertisementPageDto getUserOrders(int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders = orderRepository.findAllByAuthor(user, pageable);
        List<MyAdvertisement> orderList = new ArrayList<>();

        orders.getContent().forEach(service -> orderList.add(orderMapper.toMyAdvertisement(service)));

        boolean isLast = orders.isLast();
        Long totalCount = orders.getTotalElements();

        return new AdvertisementPageDto(orderList, isLast, totalCount);
    }

    @Override
    public String hideOrder(Long orderId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);

        if (!order.getIsVisible()) {
            throw new ResourceAlreadyExistsException("Order is already hidden");
        }
        if (!order.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("Only authors can hide their advertisements");
        }
        order.setIsVisible(false);
        orderRepository.save(order);

        return "Order is now invisible in marketplace";
    }

    @Override
    public String sendRequestToExecuteOrder(Long orderId, Authentication authentication) {

        Order order = findOrderById(orderId);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
        Organization usersOrganization = organizationEmployee.getOrganization();

        if (order.getAuthor().equals(user)) {
            throw new ResourceAlreadyExistsException("User can't execute to his/her own order");
        }
        if (order.getOrganizationCandidates().stream()
                .anyMatch(org -> org.getId().equals(usersOrganization.getId()))) {
            throw new ResourceAlreadyExistsException("User's organization already sent the request");
        }
        if (order.getOrganizationExecutor() == null) {
            if (organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.SEND_REQUEST_TO_EXECUTE_ORDER, user.getEmail())) {
                if (!order.getOrganizationCandidates().contains(usersOrganization)) {
                    order.getOrganizationCandidates().add(usersOrganization);
                    orderRepository.save(order);
                }
                return "User has left a request to execute the order";
            } else {
                throw new NoPermissionException("User has no permission to send request to execute order");
            }
        } else {
            throw new ResourceAlreadyExistsException("Order is already taken by another organization");
        }
    }

    private List<OrderCard> extractOrdersByStatusAndMap(OrderStatus status, List<Order> orders) {
        List<OrderCard> orderToReturn = new ArrayList<>();
        orders.forEach(order -> {
            if (order.getStatus() == status) {
                orderToReturn.add(orderMapper.toOrderCard(order));
            }
        });
        return orderToReturn;
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