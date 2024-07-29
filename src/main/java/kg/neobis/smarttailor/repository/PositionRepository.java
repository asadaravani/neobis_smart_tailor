package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Boolean existsPositionByNameAndOrganization(String name, Organization organization);

    Position getByName(String positionName);
}