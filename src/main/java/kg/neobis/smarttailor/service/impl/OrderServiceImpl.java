package kg.neobis.smarttailor.service.impl;

import com.cloudinary.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.dtos.ads.order.OrderRequestDto;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.OrderStatus;
import kg.neobis.smarttailor.enums.PlusMinus;
import kg.neobis.smarttailor.exception.*;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.mapper.OrderMapper;
import kg.neobis.smarttailor.repository.OrderRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.FCMService;
import kg.neobis.smarttailor.service.NotificationService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.OrganizationService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    AppUserMapper appUserMapper;
    FCMService fcmService;
    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    ObjectMapper objectMapper;
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    OrganizationService organizationService;
    OrganizationEmployeeService organizationEmployeeService;
    Validator validator;
    NotificationService notificationService;

    @Override
    public String addOrder(String orderRequestDto, List<MultipartFile> images, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrderRequestDto requestDto = parseAndValidateOrderRequestDto(orderRequestDto);
        List<Image> orderImages = cloudinaryService.saveImages(images);

        Order order = orderMapper.orderRequestDtoToEntity(requestDto, orderImages, user);

        orderRepository.save(order);

        return "Order has been created";
    }

    @Override
    @Transactional
    public String assignEmployeeToOrder(Long orderId, Long employeeId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);
        AppUser employee = appUserService.findUserById(employeeId);

        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.ASSIGN_EMPLOYEE_TO_ORDER, user.getEmail());
        OrganizationEmployee authenticatedOrganizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        OrganizationEmployee assignedToOrderEmployee = organizationEmployeeService.findByEmployeeEmail(employee.getEmail());

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
    public String assignExecutorToOrder(Long orderId, Long executorId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);
        AppUser executor = appUserService.findUserById(executorId);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(executor.getEmail());
        Organization executorOrganization = organizationEmployee.getOrganization();

        if (!order.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("User cannot manage an order that is not his own");
        }
        if (order.getOrganizationExecutor() != null) {
            throw new ResourceAlreadyExistsException(String.format("Order is already given to '%s' organization", executorOrganization.getName()));
        }
        if (order.getCandidates().stream().noneMatch(org -> org.getId().equals(executor.getId()))) {
            throw new ResourceNotFoundException(String.format("User %s hasn't sent request to execute the order", executor.getFullName()));
        }
        order.getOrderEmployees().add(executor);
        order.setOrganizationExecutor(organizationEmployee.getOrganization());
        order.setMainEmployeeExecutor(executor);
        order.setDateOfStart(LocalDate.now());
        order.setStatus(OrderStatus.WAITING);
        order.setCandidates(null);
        order.setIsVisible(false);
        orderRepository.save(order);

        return String.format("Order has been assigned to %s from '%s' organization", executor.getFullName(), organizationEmployee.getOrganization().getName());
    }

    @Override
    public String changeOrderStatus(Long orderId, PlusMinus plusMinus, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);

        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.CHANGE_ORDER_STATUS, user.getEmail());

        OrderStatus[] statusArray = OrderStatus.values();
        int currentIndex = Arrays.asList(statusArray).indexOf(order.getStatus());

        if (hasRights) {
            if (!order.getOrganizationExecutor().getId()
                    .equals(organizationEmployee.getOrganization().getId())) {
                throw new NoPermissionException("Order hasn't been given for authenticated user's organization");
            }
            if (!order.getMainEmployeeExecutor().getId()
                    .equals(user.getId())) {
                throw new NoPermissionException("Only employee that take the order can change it status. Main employee for this order: ".concat(order.getMainEmployeeExecutor().getFullName()));
            }
            if ((plusMinus == PlusMinus.MINUS && order.getStatus() == OrderStatus.WAITING)
                    || (plusMinus == PlusMinus.PLUS && order.getStatus() == OrderStatus.ARRIVED)) {
                throw new InvalidRequestException("Invalid Request");
            }
            if (plusMinus == PlusMinus.PLUS && currentIndex < statusArray.length - 1) {
                order.setStatus(statusArray[currentIndex + 1]);

            } else if (plusMinus == PlusMinus.MINUS && currentIndex > 0) {
                order.setStatus(statusArray[currentIndex - 1]);
            } else {
                throw new InvalidRequestException("Cannot change status further");
            }
        } else {
            throw new NoPermissionException("User has no permission to change order status");
        }
        orderRepository.save(order);

        int newStatusIndex;
        if (plusMinus == PlusMinus.MINUS) newStatusIndex = currentIndex - 1;
        else newStatusIndex = currentIndex + 1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String notificationMessage = String.format("Статус заказа изменен с '%s' на '%s'", statusArray[currentIndex], statusArray[newStatusIndex]);

        notificationService.sendNotification(
                new NotificationDto("Статус заказа изменено!", notificationMessage, LocalDateTime.now().format(formatter))
        );
        fcmService.sendMessageToToken(new FirebaseNotificationRequest("Статус заказа изменено!", notificationMessage, "656565ufg"));

        return String.format("Order status has been changed from '%s' to '%s'", statusArray[currentIndex], statusArray[newStatusIndex]);
    }

    @Override
    public String completeOrder(Long orderId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);

        if (order.getOrganizationExecutor() == null) {
            throw new ResourceNotFoundException("Customer hasn't chosen an executor to order yet");
        }
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.COMPLETE_ORDER, user.getEmail());
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        if (!order.getMainEmployeeExecutor().getId().equals(user.getId())) {
            throw new NoPermissionException("Only employee that take the order can complete it. Main employee for this order: ".concat(order.getMainEmployeeExecutor().getFullName()));
        }
        if (hasRights) {
            if (organizationEmployee.getOrganization().getId().
                    equals(order.getOrganizationExecutor().getId())) {
                if (order.getDateOfCompletion() == null) {
                    if (order.getStatus() == OrderStatus.ARRIVED) {
                        order.setDateOfCompletion(LocalDate.now());
                        order.setStatus(OrderStatus.COMPLETED);
                        orderRepository.save(order);

                        return "Order has been completed";
                    } else {
                        throw new InvalidRequestException("Order can't be completed is status is not 'ARRIVED'");
                    }
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
        order.setCandidates(null);
        order.setOrderEmployees(null);
        order.setOrganizationExecutor(null);
        order.setMainEmployeeExecutor(null);
        orderRepository.delete(order);

        return "Order has been deleted";
    }

    @Override
    public List<Order> findAllByCandidate(AppUser user) {
        return orderRepository.findAllByCandidate(user);
    }

    @Override
    public List<Order> findAllByUser(AppUser user) {
        return orderRepository.findAllByAuthor(user);
    }

    @Override
    public List<Order> findCompletedUserOrders(AppUser user) {
        return orderRepository.findCompletedEmployeeOrders(user);
    }

    @Override
    public List<Order> findCurrentUserOrders(AppUser user) {
        return orderRepository.findCurrentEmployeeOrders(user);
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<Order> findUserOrderPurchases(AppUser user) {
        return orderRepository.findUserOrderPurchases(user);
    }

    @Override
    public AdvertisementPageDto getAllVisibleOrders(int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders = orderRepository.findByIsVisible(true, pageable);
        List<Order> ordersList = orders.getContent();
        boolean isLast = orders.isLast();
        Long totalCount = orders.getTotalElements();

        List<OrderListDto> orderListDto = orderMapper.entityListToDtoList(ordersList);

        return new AdvertisementPageDto(orderListDto, isLast, totalCount);
    }

    @Override
    public CurrentOrganizationOrders getCurrentOrdersOfOrganization(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Organization organization = organizationEmployeeService.findByEmployeeEmail(user.getEmail()).getOrganization();
        List<Order> orders = orderRepository.findAllByOrganizationExecutorAndDateOfCompletionIsNull(organization);
        List<AppUser> employees = organizationEmployeeService.findEmployeesByOrganization(organization);
        List<Order> notConfirmedOrders = orderRepository.findAllByCandidates(employees);

        return new CurrentOrganizationOrders(
                extractOrdersByStatusAndMap(OrderStatus.NOT_CONFIRMED, notConfirmedOrders),
                extractOrdersByStatusAndMap(OrderStatus.WAITING, orders),
                extractOrdersByStatusAndMap(OrderStatus.IN_PROGRESS, orders),
                extractOrdersByStatusAndMap(OrderStatus.CHECKING, orders),
                extractOrdersByStatusAndMap(OrderStatus.SENDING, orders),
                extractOrdersByStatusAndMap(OrderStatus.ARRIVED, orders)
        );
    }

    @Override
    public CurrentOrderDetailed getCurrentOrderDetailed(Long orderId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Order order = findOrderById(orderId);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        if (!order.getOrganizationExecutor().getId().equals(organizationEmployee.getOrganization().getId())) {
            throw new UserNotInOrganizationException("User can't get order's detailed information because (s)he is not member of organization executor");
        }

        return orderMapper.entityToCurrentOrderDetailed(order);
    }

    @Override
    public EmployeePageDto getEmployeeOrdersByStage(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee authenticatedOrganizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
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
    public OrderDetailed getOrderDetailed(Long id) {
        Order order = findOrderById(id);
        return orderMapper.entityToOrderDetailed(order);
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
    public List<EmployeeOrderListDto> getOrderInfoByEmployee(AppUser employee) {
        return orderRepository.findUserOrderPurchases(employee)
                .stream()
                .map(order -> new EmployeeOrderListDto(order.getId(), order.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationOrdersDto> getOrdersOfOrganization(String email) {
        Organization organization = organizationService.findOrganizationByDirectorEmail(email);
        List<Order> orders = orderRepository.findAllByOrganizationExecutor(organization);
        return orders.stream().map(
                orderMapper::toOrganizationOrders
        ).toList();
    }

    @Override
    public OrganizationPageDto getOrganizationOrdersByStage(String stage, int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = organizationEmployee.getOrganization();

        Page<Order> organizationOrders;
        if (stage.equals("current")) {

            List<Order> orders = orderRepository.findAllByOrganizationExecutorAndDateOfCompletionIsNull(organization);
            List<AppUser> employees = organizationEmployeeService.findEmployeesByOrganization(organization);
            List<Order> notConfirmedOrders = orderRepository.findAllByCandidates(employees);

            List<Order> allOrders = new ArrayList<>();
            allOrders.addAll(orders);
            allOrders.addAll(notConfirmedOrders);

            allOrders.sort((dto1, dto2) -> dto2.getCreatedAt().compareTo(dto1.getCreatedAt()));

            int start = pageNumber * pageSize;
            int end = Math.min(start + pageSize, allOrders.size());

            List<Order> paginatedList;
            if (start >= allOrders.size()) {
                paginatedList = new ArrayList<>();
            } else {
                paginatedList = allOrders.subList(start, end);
            }

            boolean isLast = end >= allOrders.size();
            long totalCount = allOrders.size();

            List<OrganizationOrdersDto> orderListDto = orderMapper.entityListToOrganizationOrderListDto(paginatedList);

            return new OrganizationPageDto(organization.getId(), organization.getName(),
                    organization.getDescription(), organization.getCreatedAt(), orderListDto, isLast, totalCount);

        } else if (stage.equals("completed")) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "dateOfCompletion");
            organizationOrders = orderRepository.findCompletedOrganizationOrders(organization, pageable);

            List<Order> ordersList = organizationOrders.getContent();
            List<OrganizationOrdersDto> orderListDto = orderMapper.entityListToOrganizationOrderListDto(ordersList);
            boolean isLast = organizationOrders.isLast();
            Long totalCount = organizationOrders.getTotalElements();
            return new OrganizationPageDto(organization.getId(), organization.getName(),
                    organization.getDescription(), organization.getCreatedAt(), orderListDto, isLast, totalCount);

        } else {
            throw new ResourceNotFoundException("Invalid state.\nValid states: completed, current");
        }
    }

    @Override
    public AdvertisementPageDto getUserOrderHistoryByStage(String stage, int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        Pageable pageable;
        Page<Order> userOrders;
        if (stage.equals("current")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfStart"));
            userOrders = orderRepository.findCurrentEmployeeOrders(user, pageable);
        } else if (stage.equals("completed")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfCompletion"));
            userOrders = orderRepository.findCompletedEmployeeOrders(user, pageable);
        } else {
            throw new ResourceNotFoundException("Invalid stage.\nValid states: completed, current");
        }

        List<UserOrderHistoryDto> orderListDto = orderMapper.entityListToUserOrderHistoryDto(userOrders, stage);
        boolean isLast = userOrders.isLast();
        Long totalCount = userOrders.getTotalElements();

        return new AdvertisementPageDto(orderListDto, isLast, totalCount);
    }

    @Override
    public AdvertisementPageDto getUserOrders(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders = orderRepository.findAllByAuthor(user, pageable);
        List<MyAdvertisement> orderList = new ArrayList<>();

        orders.getContent().forEach(order -> orderList.add(orderMapper.toMyAdvertisement(order)));

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
    @Transactional
    public String sendRequestToExecuteOrder(Long orderId, Authentication authentication) {

        Order order = findOrderById(orderId);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        Organization usersOrganization = organizationEmployee.getOrganization();

        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.TAKE_ORDER, user.getEmail());

        if (!hasRights) {
            throw new NoPermissionException("User has no permission to take order");
        }
        if (order.getAuthor().getId().equals(user.getId())) {
            throw new SelfPurchaseException("Users can't respond to theirs advertisements");
        }
        if (order.getAuthor().getId().equals(usersOrganization.getDirector().getId())) {
            throw new SelfPurchaseException("Order's author is a director of user's organization");
        }
        if (order.getOrganizationExecutor() != null) {
            throw new ResourceAlreadyExistsException("Order has been already given to another organization");
        }
        if (order.getCandidates().stream().
                anyMatch(candidate -> candidate.getId().equals(user.getId()))) {
            throw new ResourceAlreadyExistsException("User already sent the request");
        }
        order.getCandidates().add(user);
        orderRepository.save(order);

        return "User has left a request to execute the order";
    }

    @Override
    public AdvertisementPageDto searchOrders(String name, int pageNumber, int pageSize, Authentication authentication) {
        appUserService.getUserFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<Order> orders = searchOrders(name, pageable);
        List<Order> orderList = orders.getContent();
        List<OrderListDto> orderCardList = orderMapper.entityListToDtoList(orderList);
        boolean isLast = orders.isLast();
        Long totalCount = orders.getTotalElements();
        return new AdvertisementPageDto(orderCardList, isLast, totalCount);
    }

    private Page<Order> searchOrders(String name, Pageable pageable) {
            return orderRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
            if (StringUtils.isBlank(requestDto.name())) {
                throw new InvalidRequestException("Name cannot be empty");
            } else if (requestDto.name().length() < 5 || requestDto.name().length() > 50) {
                throw new InvalidRequestException("Name size must be between 2 and 50");
            }
            if (StringUtils.isBlank(requestDto.description())) {
                throw new InvalidRequestException("Description cannot be empty");
            } else if (requestDto.description().length() < 5 || requestDto.description().length() > 1000) {
                throw new InvalidRequestException("Description size must be between 2 and 1000");
            }
            if (requestDto.price() == null || requestDto.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidRequestException("Price must be greater than zero");
            }
            if (StringUtils.isBlank(requestDto.contactInfo())) {
                throw new InvalidRequestException("Contact info cannot be empty");
            } else if (requestDto.contactInfo().length() > 320) {
                throw new InvalidRequestException("Contact info's size cannot be greater than 320");
            }
            if (requestDto.dateOfExecution() == null) {
                throw new InvalidRequestException("Date of execution cannot be empty");
            } else if (requestDto.dateOfExecution().isBefore(LocalDate.now())) {
                throw new InvalidRequestException("Date of execution must be after today");
            }
            if (requestDto.items().isEmpty()) {
                throw new InvalidRequestException("Items must not be empty");
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }


    @Override
    public AdvertisementPageDto getOrganizationOrderHistory(int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfCompletion"));
        Page<Order> organizationCompletedOrders = orderRepository.findCompletedEmployeeOrders(user, pageable);

        List<UserOrderHistoryDto> orderListDto = orderMapper.entityListToUserOrderHistoryDto(organizationCompletedOrders, "completed");
        boolean isLast = organizationCompletedOrders.isLast();
        Long totalCount = organizationCompletedOrders.getTotalElements();

        return new AdvertisementPageDto(orderListDto, isLast, totalCount);
    }

    @Override
    public OrganizationOrderHistoryDetailedDto getOrganizationOrderHistoryDetailed(Long orderId, Authentication authentication) {

        Order order = findOrderById(orderId);

        return OrganizationOrderHistoryDetailedDto.builder()
                .id(order.getId())
                .name(order.getName())
                .description(order.getDescription())
                .price(order.getPrice())
                .dateOfCompletion(order.getDateOfCompletion())
                .employees(order.getOrderEmployees().stream()
                        .map(appUserMapper::entityToEmployeeDto)
                        .collect(Collectors.toList()))
                .authorFullName(order.getFullName(order))
                .authorContactInfo(order.getContactInfo())
                .build();
    }

    @Override
    public AdvertisementPageDto getOrganizationOrderHistoryByEmployee(Long employeeId, String stage, int pageNumber, int pageSize, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee authenticatedOrganizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = authenticatedOrganizationEmployee.getOrganization();
        AppUser employee = appUserService.findUserById(employeeId);

        Boolean isEmployeeInOrganization = organizationEmployeeService.existsByOrganizationAndEmployeeEmail(organization, employee.getEmail());
        if (!isEmployeeInOrganization) {
            throw new UserNotInOrganizationException("Employee is not a member of authenticated user organization");
        }
        Pageable pageable;
        Page<Order> employeeOrders;
        switch (stage) {
            case "current" -> {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfStart"));
                employeeOrders = orderRepository.findCurrentEmployeeOrders(employee, pageable);
            }
            case "completed" -> {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfCompletion"));
                employeeOrders = orderRepository.findCompletedEmployeeOrders(employee, pageable);
            }
            case "all" -> {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateOfExecution"));
                employeeOrders = orderRepository.findAllByEmployee(employee, pageable);
            }
            default -> throw new ResourceNotFoundException("Invalid stage.\nValid states: completed, current");
        }
        List<OrganizationOrderHistoryPage> orderListDto = orderMapper.entityListToOrganizationOrderHistoryPage(employeeOrders, stage);
        boolean isLast = employeeOrders.isLast();
        Long totalCount = employeeOrders.getTotalElements();
        return new AdvertisementPageDto(orderListDto, isLast, totalCount);
    }
}