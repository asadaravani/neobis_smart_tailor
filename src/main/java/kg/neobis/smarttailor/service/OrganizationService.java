package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.dtos.OrganizationDetailed;
import kg.neobis.smarttailor.entity.Organization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {

    ResponseEntity<?> acceptInvitation(String invitingToken);

    String createOrganization(String organizationDto, MultipartFile organizationImage, Authentication authentication);

    Organization findOrganizationByDirectorOrEmployee(String email);

    OrganizationDetailed getOrganization(Authentication authentication);

    Organization getOrganizationByDirectorEmail(String email);

    Organization getOrganizationByName(String organizationName);

    String sendInvitation(String request, Authentication authentication) throws MessagingException;
}