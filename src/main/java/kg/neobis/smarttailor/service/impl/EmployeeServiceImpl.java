package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.AdvertisementCard;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.EmployeeDetailedDto;
import kg.neobis.smarttailor.dtos.EmployeeDto;
import kg.neobis.smarttailor.dtos.EmployeeListDto;
import kg.neobis.smarttailor.dtos.EmployeeOrderListDto;
import kg.neobis.smarttailor.dtos.EmployeesPageDto;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.EmployeeService;
import kg.neobis.smarttailor.service.OrderService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {

    AppUserMapper appUserMapper;
    AppUserService appUserService;
    OrderService orderService;
    OrganizationEmployeeService organizationEmployeeService;

    @Override
    public List<EmployeeListDto> getAllEmployees(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = organizationEmployee.getOrganization();
        List<OrganizationEmployee> organizationEmployees = organizationEmployeeService.findAllByOrganization(organization);

        return organizationEmployees.stream().map(orgEmp -> {
            List<EmployeeOrderListDto> orderNames = orderService.getOrderInfoByEmployee(orgEmp.getEmployee());
            return appUserMapper.entityListToEmployeeListDto(orgEmp, orderNames);
        }).collect(Collectors.toList());
    }

    @Override
    public EmployeeDetailedDto getEmployeeDetailed(Long employeeId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        AppUser employee = appUserService.findUserById(employeeId);
        OrganizationEmployee employeeData = organizationEmployeeService.findByEmployeeEmail(employee.getEmail());

        Organization employeeOrganization = employeeData.getOrganization();

        Boolean isAuthenticatedUserInOrganization = organizationEmployeeService.existsByOrganizationAndEmployeeEmail(employeeOrganization, user.getEmail());

        if (!isAuthenticatedUserInOrganization) {
            throw new UserNotInOrganizationException("Authenticated user is not a member of employee's organization");
        }

        return appUserMapper.entityToEmployeeDetailedDto(user, employeeData.getPosition().getName());
    }

    @Override
    public EmployeesPageDto searchEmployees(String query, int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = organizationEmployee.getOrganization();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<OrganizationEmployee> employees = organizationEmployeeService.findEmployee(query, query, query, organization, pageable);

        List<OrganizationEmployee> employeeList = employees.getContent();
        List<EmployeeListDto> employeeListDto = employeeList.stream().map(orgEmp -> {
            List<EmployeeOrderListDto> orderNames = orderService.getOrderInfoByEmployee(orgEmp.getEmployee());
            return appUserMapper.entityListToEmployeeListDto(orgEmp, orderNames);
        }).collect(Collectors.toList());
        boolean isLast = employees.isLast();
        Long totalCount = employees.getTotalElements();
        return new EmployeesPageDto(employeeListDto, isLast, totalCount);
    }

    @Override
    public AdvertisementPageDto searchAds(String query, int pageNumber, int pageSize, Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = organizationEmployee.getOrganization();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<AdvertisementCard> result = organizationEmployeeService.searchAcrossTable(query, user.getId(), organization.getId(), pageable);
        List<AdvertisementCard> advertisementCardList = result.getContent();
        boolean isLast = result.isLast();
        Long totalCount = result.getTotalElements();

        return new AdvertisementPageDto(advertisementCardList, isLast, totalCount);
    }

    @Override
    public List<EmployeeDto> getAvailableEmployees(Long orderId, Authentication authentication) {

        orderService.findOrderById(orderId);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        List<AppUser> employees = organizationEmployeeService.findEmployeesWithPositionWeightLessThan(
                organizationEmployee.getPosition().getWeight(),
                organizationEmployee.getOrganization().getId(),
                orderId
        );

        return employees.stream().map(appUserMapper::entityToEmployeeDto).toList();
    }

    public String removeEmployee(Long employeeId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        AppUser employee = appUserService.findUserById(employeeId);

        OrganizationEmployee userInfo = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        OrganizationEmployee employeeInfo = organizationEmployeeService.findByEmployeeEmail(employee.getEmail());

        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.REMOVE_EMPLOYEE, user.getEmail());

        Organization userOrganization = userInfo.getOrganization();
        Organization employeeOrganization = employeeInfo.getOrganization();

        List<Order> employeeOrders = orderService.findCurrentUserOrders(employee);

        if (hasRights) {
            if (userOrganization.getId().equals(employeeOrganization.getId())) {
                if (userInfo.getPosition().getWeight() > employeeInfo.getPosition().getWeight()) {
                    if (employeeOrders == null) {
                        List<Order> employeeCompletedOrders = orderService.findCompletedUserOrders(employee);
                        for (Order order: employeeCompletedOrders) {
                            order.getOrderEmployees().remove(employee);
                            if (order.getMainEmployeeExecutor().getId().equals(employee.getId())) {
                                order.setMainEmployeeExecutor(null);
                            }
                        }
                        List<Order> employeeRequests = orderService.findAllByCandidate(employee);
                        for (Order order: employeeRequests) {
                            order.getCandidates().remove(employee);
                        }
                        employeeInfo.setEmployee(null);
                        employeeInfo.setPosition(null);
                        employeeInfo.setOrganization(null);

                        organizationEmployeeService.delete(employeeInfo);

                        return "Employee has been removed from organization";
                    } else {
                        throw new InvalidRequestException("User has uncompleted orders");
                    }
                } else {
                    throw new InvalidRequestException("Authenticated user's position is lower than employee's in hierarchy");
                }
            } else {
                throw new UserNotInOrganizationException("Authenticated user and employee are not the members of the same organization");
            }
        } else {
            throw new NoPermissionException("User has no permission to remove employee from organization");
        }
    }
}
