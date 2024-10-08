package kg.neobis.smarttailor.service.impl;

import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.dtos.AdvertisementCard;
import kg.neobis.smarttailor.dtos.MyAdvertisementCard;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.entity.Services;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.repository.OrganizationEmployeeRepository;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationEmployeeServiceImpl implements OrganizationEmployeeService {

    OrganizationEmployeeRepository organizationEmployeeRepository;

    @Override
    @Transactional
    public void delete(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.delete(organizationEmployee);
    }

    @Override
    public Boolean existsByAccessRightAndEmployeeEmail(AccessRight accessRight, String employeeEmail) {
        return organizationEmployeeRepository.existsByPosition_AccessRightsIsContainingAndEmployeeEmail(accessRight, employeeEmail);
    }

    @Override
    public Boolean existsByEmployeeEmail(String email) {
        return organizationEmployeeRepository.existsByEmployeeEmail(email);
    }

    @Override
    public Boolean existsByOrganizationAndEmployeeEmail(Organization organization, String employeeEmail) {
        return organizationEmployeeRepository.existsByOrganizationAndEmployeeEmail(organization, employeeEmail);
    }

    @Override
    public Page<OrganizationEmployee> findEmployee(String name, String surname, String patronymic, Organization organization, Pageable pageable) {
        return organizationEmployeeRepository.findByEmployeeNameContainingIgnoreCaseOrEmployeeSurnameContainingIgnoreCaseOrEmployeePatronymicContainingIgnoreCaseAndOrganization(name, surname, patronymic, organization, pageable);
    }


    @Override
    public Page<MyAdvertisementCard> searchAcrossTable(String query, Long userId, Pageable pageable) {

        List<Services> serviceResults = organizationEmployeeRepository.searchServices(query, userId);
        List<Order> orderResults = organizationEmployeeRepository.searchOrders(query, userId);
        List<Equipment> equipmentResults = organizationEmployeeRepository.searchEquipments(query, userId);

        List<MyAdvertisementCard> advertisementCards = new ArrayList<>();

        for (Services service : serviceResults) {
            MyAdvertisementCard card = new MyAdvertisementCard(service.getId(), service.getFirstImage(service.getImages()), "Услуги", service.getName(), service.getDescription());
            advertisementCards.add(card);
        }

        for (Order order : orderResults) {
            MyAdvertisementCard card = new MyAdvertisementCard(order.getId(), order.getFirstImage(order.getImages()),"Заказ", order.getName(), order.getDescription());
            advertisementCards.add(card);
        }

        for (Equipment equipment : equipmentResults) {
            MyAdvertisementCard card = new MyAdvertisementCard(equipment.getId(), equipment.getFirstImage(equipment.getImages()),"Оборудование", equipment.getName(), equipment.getDescription());
            advertisementCards.add(card);
        }

        int start = Math.min((int) pageable.getOffset(), advertisementCards.size());
        int end = Math.min((start + pageable.getPageSize()), advertisementCards.size());
        List<MyAdvertisementCard> paginatedList = advertisementCards.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, advertisementCards.size());
    }



    @Override
    public List<OrganizationEmployee> findAllByOrganization(Organization organization) {
        return organizationEmployeeRepository.findAllByOrganization(organization);
    }

    @Override
    public OrganizationEmployee findByEmployeeEmail(String email){
        return organizationEmployeeRepository.findByEmployeeEmail(email)
                .orElseThrow(() -> new UserNotInOrganizationException("User is not a member of any organization"));
    }

    @Override
    public List<AppUser> findEmployeesWithPositionWeightLessThan(int weight, Long organizationId, Long orderId) {
        return organizationEmployeeRepository.findUnassignedEmployeesWithPositionWeightLessThanInOrganization(organizationId, weight, orderId);
    }

    @Override
    public List<AppUser> findEmployeesByOrganization(Organization organization) {
        return organizationEmployeeRepository.findEmployeesByOrganization(organization);
    }

    @Override
    public void save(OrganizationEmployee organizationEmployee) {
        organizationEmployeeRepository.save(organizationEmployee);
    }
}