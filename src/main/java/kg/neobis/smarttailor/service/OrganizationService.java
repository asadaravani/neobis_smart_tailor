package kg.neobis.smarttailor.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {

    String createOrganization(String organizationDto, MultipartFile organizationImage, Authentication authentication);
}