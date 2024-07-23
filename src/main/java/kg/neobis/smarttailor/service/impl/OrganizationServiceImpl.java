package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.dtos.EmployeeInvitationRequest;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.NoPermissionException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.OrganizationMapper;
import kg.neobis.smarttailor.repository.OrganizationRepository;
import kg.neobis.smarttailor.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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
            return ResponseEntity.ok("you accepted invitation");
        }
        return ResponseEntity.badRequest().body("token has been expired");
    }

    @Override
    public String createOrganization(String organizationDto, MultipartFile organizationImage, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (!user.getHasSubscription())
            throw new ResourceNotFoundException("user has no subscription", HttpStatus.NOT_FOUND);

        if (organizationRepository.existsOrganizationByDirector(user))
            throw new ResourceAlreadyExistsException("user already has an organization", HttpStatus.CONFLICT);

        OrganizationDto requestDto = parseAndValidateOrganizationDto(organizationDto);
        if (organizationRepository.existsOrganizationByName(requestDto.name()))
            throw new ResourceAlreadyExistsException("organization with name \"".concat(requestDto.name().concat("\" already exists")), HttpStatus.CONFLICT);

        Image image = cloudinaryService.saveImage(organizationImage);

        Organization organization = organizationMapper.dtoToEntity(requestDto, image, user);
        organizationRepository.save(organization);
        return "organization has been created";
    }

    @Override
    public Organization getOrganizationByDirectorEmail(String email) {
        return organizationRepository.getByDirectorEmail(email);
    }

    @Override
    public String sendInvitation(String request, Authentication authentication) throws MessagingException {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Boolean hasRights = organizationEmployeeService.existsByPositionNameAndEmployeeEmail(AccessRight.ADD_EMPLOYEE.name(), user.getEmail());
        EmployeeInvitationRequest employeeInvitationRequest = parseAndValidateEmployeeInvitationRequest(request);
        Boolean isUserExists = appUserService.existsUserByEmail(employeeInvitationRequest.email());
        AppUser employee;

        if (user.getHasSubscription() || hasRights) {
            if (isUserExists) {
                if (organizationRepository.existsOrganizationByDirectorEmail(employeeInvitationRequest.email()))
                    throw new ResourceAlreadyExistsException("user has his own organization", HttpStatus.CONFLICT);

                if (organizationEmployeeService.existsByEmployeeEmail(employeeInvitationRequest.email()))
                    throw new ResourceAlreadyExistsException("user is already a member of another organization", HttpStatus.CONFLICT);
            }
            var organization = getOrganizationByDirectorEmail(user.getEmail());
            if (organization == null)
                throw new ResourceNotFoundException("user has no organization", HttpStatus.NOT_FOUND);

            Position position = positionService.getPositionByName(employeeInvitationRequest.position());
            if (position == null)
                throw new ResourceNotFoundException("specified position not found", HttpStatus.NOT_FOUND);

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
            } else
                employee = appUserService.findUserByEmail(employeeInvitationRequest.email());

            InvitationToken invitationToken = invitationTokenService.generateInvitationToken(employee, organization, position);
            MimeMessage message = emailService.createInvitationEmployeeMail(employee, invitationToken, organization, position);
            emailService.sendEmail(message);

            return "invitation has been sent";
        }
        throw new NoPermissionException("user has no permission to invite employee", HttpStatus.FORBIDDEN);
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
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}