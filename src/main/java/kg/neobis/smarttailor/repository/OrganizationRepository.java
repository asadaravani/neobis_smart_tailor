package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Boolean existsOrganizationByDirector(AppUser director);

    Boolean existsOrganizationByDirectorEmail(String email);

    Boolean existsOrganizationByName(String name);

    Organization getByDirectorEmail(String email);

    Optional<Organization> findByDirectorEmail(String email);

    Optional<Organization> findByName(String name);
}