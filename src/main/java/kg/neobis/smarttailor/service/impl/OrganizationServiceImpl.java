package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.dtos.EmployeeInvitationRequest;
import kg.neobis.smarttailor.dtos.OrganizationDetailed;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.InvitationToken;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.OrganizationEmployee;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.exception.UserNotInOrganizationException;
import kg.neobis.smarttailor.mapper.OrganizationMapper;
import kg.neobis.smarttailor.repository.OrganizationRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.InvitationTokenService;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import kg.neobis.smarttailor.service.OrganizationService;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationServiceImpl implements OrganizationService {

    AppUserService appUserService;
    CloudinaryService cloudinaryService;
    EmailService emailService;
    InvitationTokenService invitationTokenService;
    ObjectMapper objectMapper;
    OrganizationEmployeeService organizationEmployeeService;
    OrganizationMapper organizationMapper;
    OrganizationRepository organizationRepository;
    PositionService positionService;
    Validator validator;

    @Override
    public ResponseEntity<?> acceptInvitation(String invitationToken) {

        InvitationToken token = invitationTokenService.findByToken(invitationToken);

        if (LocalDateTime.now().isBefore(token.getExpirationTime())) {

            AppUser user = token.getUser();
            user.setEnabled(true);
            appUserService.save(user);

            invitationTokenService.delete(token);

            OrganizationEmployee organizationEmployee = OrganizationEmployee.builder()
                    .organization(token.getOrganization())
                    .position(token.getPosition())
                    .employee(user)
                    .build();
            organizationEmployeeService.save(organizationEmployee);

            return ResponseEntity.ok("You accepted invitation");
        }
        return ResponseEntity.badRequest().body("Token has been expired");
    }

    @Override
    public String createOrganization(String organizationRequestDto, MultipartFile organizationImage, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (!user.getHasSubscription()) {
            throw new NoPermissionException("User has no subscription");
        }

        if (organizationRepository.existsOrganizationByDirector(user)) {
            throw new ResourceAlreadyExistsException("User already has an organization");
        }

        OrganizationDto requestDto = parseAndValidateOrganizationDto(organizationRequestDto);
        if (organizationRepository.existsOrganizationByName(requestDto.name())) {
            throw new ResourceAlreadyExistsException("Organization with name \"".concat(requestDto.name().concat("\" already exists")));
        }

        Image image = cloudinaryService.saveImage(organizationImage);
        Organization organization = organizationMapper.dtoToEntity(requestDto, image, user);
        organizationRepository.save(organization);

        Set<AccessRight> accessRights = EnumSet.allOf(AccessRight.class);

        Position directorPosition = Position.builder()
                .name("Директор")
                .weight(5)
                .accessRights(accessRights)
                .organization(organization)
                .build();
        positionService.save(directorPosition);

        OrganizationEmployee organizationEmployee = OrganizationEmployee.builder()
                .organization(organization)
                .position(directorPosition)
                .employee(user)
                .build();
        organizationEmployeeService.save(organizationEmployee);

        return "Organization has been created";
    }

    @Override
    public Organization findOrganizationByDirectorEmail(String email) {
        return organizationRepository.findByDirectorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    @Override
    public OrganizationDetailed getOrganization(Authentication authentication) {
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        return organizationMapper.toOrganizationDetailed(findOrganizationByDirectorEmail(user.getEmail()));
    }

    @Override
    public Organization getOrganizationByName(String organizationName) {
        return organizationRepository.findByName(organizationName)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    @Override
    public String sendInvitation(String request, Authentication authentication) throws MessagingException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        OrganizationEmployee organizationEmployee = organizationEmployeeService.findByEmployeeEmail(user.getEmail())
                .orElseThrow(() -> new UserNotInOrganizationException("Authenticated user is not a member of any organization "));
        EmployeeInvitationRequest employeeInvitationRequest = parseAndValidateEmployeeInvitationRequest(request);
        Boolean hasRights = organizationEmployeeService.existsByAccessRightAndEmployeeEmail(AccessRight.INVITE_EMPLOYEE, user.getEmail());
        Boolean isUserExists = appUserService.existsUserByEmail(employeeInvitationRequest.email());

        AppUser employee;
        if (hasRights) {
            if (isUserExists) {
                if (organizationRepository.existsOrganizationByDirectorEmail(employeeInvitationRequest.email())) {
                    throw new ResourceAlreadyExistsException("User has his own organization");
                }
                if (organizationEmployeeService.existsByOrganizationAndEmployeeEmail(organizationEmployee.getOrganization(), employeeInvitationRequest.email())) {
                    throw new ResourceAlreadyExistsException("User is already a member of your organization");
                }
                if (organizationEmployeeService.existsByEmployeeEmail(employeeInvitationRequest.email())) {
                    throw new ResourceAlreadyExistsException("User is already a member of another organization");
                }
            }

            Position position = positionService.getPositionByName(employeeInvitationRequest.position());
            if (position == null) {
                throw new ResourceNotFoundException("Specified position not found");
            }
            if (!isUserExists) {
                employee = AppUser.builder()
                        .surname(employeeInvitationRequest.surname())
                        .name(employeeInvitationRequest.name())
                        .patronymic(employeeInvitationRequest.patronymic())
                        .email(employeeInvitationRequest.email())
                        .phoneNumber(employeeInvitationRequest.phoneNumber())
                        .role(Role.USER)
                        .image(new Image("https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg"))
                        .enabled(false)
                        .hasSubscription(false)
                        .build();
                employee = appUserService.save(employee);
            } else {
                employee = appUserService.findUserByEmail(employeeInvitationRequest.email());
            }
            InvitationToken invitationToken = invitationTokenService.findByUser(employee);
            if (invitationToken != null) {
                invitationTokenService.delete(invitationToken);
            }
            invitationToken = invitationTokenService.generateInvitationToken(employee, organizationEmployee.getOrganization(), position);
            MimeMessage message = emailService.createInvitationEmployeeMail(user, employee.getEmail(), invitationToken, organizationEmployee.getOrganization(), position);
            emailService.sendEmail(message);

            return "Invitation has been sent";
        }
        throw new NoPermissionException("User has no permission to invite employee");
    }

    private EmployeeInvitationRequest parseAndValidateEmployeeInvitationRequest(String invitation) {
        try {
            EmployeeInvitationRequest requestDto = objectMapper.readValue(invitation, EmployeeInvitationRequest.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "invitation");
            validator.validate(invitation, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    private OrganizationDto parseAndValidateOrganizationDto(String organizationDto) {
        try {
            OrganizationDto requestDto = objectMapper.readValue(organizationDto, OrganizationDto.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "orderDto");
            validator.validate(organizationDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }
}