package kg.neobis.smarttailor.repository;

import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Boolean existsPositionByNameAndOrganization(String name, Organization organization);

    @Query("SELECT p FROM Position p LEFT JOIN FETCH p.accessRights WHERE p.name != 'Директор' AND p.organization = :organization")
    List<Position> findAllPositionsExceptDirector(@Param("organization") Organization organization);

    List<Position> findAllByOrganizationAndWeightIsLessThan(Organization organization, Integer weight);

    Position getByName(String positionName);
}