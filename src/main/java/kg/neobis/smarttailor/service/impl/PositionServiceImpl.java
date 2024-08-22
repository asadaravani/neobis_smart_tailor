package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.PlusMinus;
import kg.neobis.smarttailor.exception.*;
import kg.neobis.smarttailor.mapper.PositionMapper;
import kg.neobis.smarttailor.repository.PositionRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionServiceImpl implements PositionService {

    AppUserService appUserService;
    ObjectMapper objectMapper;
    OrganizationEmployeeService organizationEmployeeService;
    PositionMapper positionMapper;
    Validator validator;
    PositionRepository positionRepository;

    @Override
    public PositionsWeightGroups getPositionsByWeight(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Organization organization = organizationEmployee.getOrganization();

        List<Position> positions = positionRepository.findAllByOrganizationAndWeightIsLessThan(organization, 6);
        return new PositionsWeightGroups(
                extractPositionsByWeightAndMap(5, positions),
                extractPositionsByWeightAndMap(4, positions),
                extractPositionsByWeightAndMap(3, positions),
                extractPositionsByWeightAndMap(2, positions),
                extractPositionsByWeightAndMap(1, positions)
        );
    }

    private List<PositionCard> extractPositionsByWeightAndMap(int weight, List<Position> positions) {
        List<PositionCard> positionCards = new ArrayList<>();
        positions.forEach(position -> {
            if (position.getWeight() == weight) {
                positionCards.add(positionMapper.toPositionCard(position));
            }
        });
        return positionCards;
    }

    @Override
    public String addPosition(String positionRequestDto, Authentication authentication) {

        PositionDto requestDto = parseAndValidatePositionDto(positionRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        int positionWeight = organizationEmployee.getPosition().getWeight();

        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.CREATE_POSITION, user.getEmail());
        Set<AccessRight> authenticatedUserAccessRights = organizationEmployee.getPosition().getAccessRights();

        if (hasRights) {
            if (!positionRepository.existsPositionByNameAndOrganization(requestDto.positionName(), organizationEmployee.getOrganization())) {
                if (requestDto.weight() < positionWeight && requestDto.weight() > 0) {
                    for (AccessRight accessRight: requestDto.accessRights()) {
                        if (!authenticatedUserAccessRights.contains(accessRight)) {
                            throw new NoPermissionException("User can't create position with rights, that he doesn't have");
                        }
                    }
                    Position position = positionMapper.dtoToEntity(requestDto, organizationEmployee.getOrganization());
                    positionRepository.save(position);

                } else {
                    throw new NoPermissionException(String.format("Authenticated user's weight: %s. Position's weight must be between 1 and %s",
                            positionWeight, positionWeight-1));
                }
            } else {
                throw new ResourceAlreadyExistsException(String.format("Position with name '%s' already exists", requestDto.positionName()));
            }
        } else {
            throw new NoPermissionException("User has no permission to create position");
        }

        return "Position has been created";
    }

    @Override
    public String changePositionWeight(Long positionId, PlusMinus plusMinus, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.CHANGE_POSITION_WEIGHT, user.getEmail());

        Integer positionWeight = position.getWeight();

        if (hasRights) {
            if (!position.getOrganization().getId()
                    .equals(organizationEmployee.getOrganization().getId())) {
                throw new NoPermissionException("Position was created by another organization");
            }
            if (plusMinus == PlusMinus.PLUS) {
                positionWeight++;
            } else if (plusMinus == PlusMinus.MINUS) {
                positionWeight--;
            }

            if (positionWeight > 4 || positionWeight < 0) {
                throw new InvalidRequestException("Position weight must be between 1 and 4");
            }

            if (positionWeight >= organizationEmployee.getPosition().getWeight()) {
                throw new InvalidRequestException("User can't give position weight more than his own");
            }
        } else {
            throw new NoPermissionException("User has no permission to change order status");
        }

        positionRepository.save(position);

        return String.format("Position weight is '%s'", positionWeight);
    }

    @Override
    public List<PositionDto> getAllPositionsExceptDirector(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        List<Position> positions = positionRepository.findAllPositionsExceptDirector(organizationEmployee.getOrganization());

        return positionMapper.entityListToDtoList(positions);
    }

    @Override
    public List<PositionDto> getAvailablePositionsForInvitation(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        List<Position> positions = positionRepository.findAllByOrganizationAndWeightIsLessThan(organizationEmployee.getOrganization(), organizationEmployee.getPosition().getWeight());

        return positionMapper.entityListToDtoList(positions);
    }

    @Override
    public List<Integer> getPositionsWithWeightsLessThan(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        List<Integer> weights = new ArrayList<>();
        for (int i = organizationEmployee.getPosition().getWeight()-1; i >= 1; i--) {
            weights.add(i);
        }

        return weights;
    }

    @Override
    public Set<AccessRight> getAvailableAccessRights(Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());

        return organizationEmployee.getPosition().getAccessRights();
    }

    @Override
    public Position getPositionByName(String name) {
        return positionRepository.getByName(name);
    }

    @Override
    public Position save(Position position) {
        return positionRepository.save(position);
    }

    private PositionDto parseAndValidatePositionDto(String positionDto) {
        try {
            PositionDto requestDto = objectMapper.readValue(positionDto, PositionDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "positionDto");
            validator.validate(positionDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    @Override
    public Set<AccessRight> getPositionAccessRights(Long positionId) {

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        return position.getAccessRights();
    }

    @Override
    public String changeAccessRights(Long positionId, UpdatePositionAccessRightsRequest request, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail());
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.CHANGE_POSITION_ACCESS_RIGHTS, user.getEmail());

        if (!hasRights) {
            throw new NoPermissionException("User has no permission to change position's access rights");
        }
        if (!position.getOrganization().getId()
                .equals(organizationEmployee.getOrganization().getId())) {
            throw new UserNotInOrganizationException("User can't change access rights for another organization's position");
        }
        if (position.getWeight() >= organizationEmployee.getPosition().getWeight()) {
            throw new NoPermissionException("User can't change access rights for position, which weight more than his");
        }

        Set<AccessRight> requestAccessRights = request.accessRights();
        Set<AccessRight> positionAccessRights = getPositionAccessRights(positionId);
        Set<AccessRight> availableAccessRights = getAvailableAccessRights(authentication);

        availableAccessRights.addAll(positionAccessRights);

        for (AccessRight accessRight: requestAccessRights) {
            if (!availableAccessRights.contains(accessRight)) {
                throw new InvalidRequestException(String.format("Unavailable access right in request: %s", accessRight));
            }
        }

        position.setAccessRights(requestAccessRights);
        positionRepository.save(position);

        return "Position's access rights has been changed";
    }
}