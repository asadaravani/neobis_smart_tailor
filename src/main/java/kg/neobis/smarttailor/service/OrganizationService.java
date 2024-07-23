package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.Organization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {

    String createOrganization(String organizationDto, MultipartFile organizationImage, Authentication authentication);

    ResponseEntity<?> acceptInvitation(String invitingToken);

    String sendInvitation(String request, Authentication authentication) throws MessagingException;

    Organization getOrganizationByDirectorEmail(String email);
}