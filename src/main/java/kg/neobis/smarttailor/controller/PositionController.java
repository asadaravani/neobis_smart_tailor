package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.dtos.PositionsWeightGroups;
import kg.neobis.smarttailor.enums.AccessRight;
import kg.neobis.smarttailor.enums.PlusMinus;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@Tag(name = "Position")
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.POSITION_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {

    PositionService positionService;

    @Operation(
            summary = "ADD POSITION",
            description = "Accepts position's name, weight and permissions to create the position in organization",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "409", description = "Position with specified name already exists | User has no permission to create position | User can't create position with rights, that he doesn't have"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/add-position")
    public ResponseEntity<String> addPosition(@RequestPart("position") String position, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.addPosition(position, authentication));
    }

    @Operation(
            summary = "GET ALL POSITIONS",
            description = "Accepts user's data and and displays a list of positions in the organization in which he is a member",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position list has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-positions")
    public ResponseEntity<List<PositionDto>> getAllPositions(Authentication authentication) {
        return ResponseEntity.ok(positionService.getAllPositionsExceptDirector(authentication));
    }

    @PutMapping("/change-position-weight/{positionId}/{plusMinus}")
    public ResponseEntity<String> changePositionWeight(@PathVariable Long positionId,
                                                       @PathVariable PlusMinus plusMinus,
                                                       Authentication authentication) {
        return ResponseEntity.ok(positionService.changePositionWeight(positionId, plusMinus, authentication));
    }

    @Operation(
            summary = "GET ACCESS RIGHTS TO CREATE POSITION",
            description = "Accepts user's data, gets access rights, which are available to give for position",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Access rights have been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/available-access-rights")
    public ResponseEntity<Set<AccessRight>> getAvailableAccessRights(Authentication authentication) {
        return ResponseEntity.ok(positionService.getAvailableAccessRights(authentication));
    }

    @Operation(
            summary = "GET POSITIONS TO INVITE EMPLOYEE",
            description = "Accepts user's data, gets positions, which are available to give for employee",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position list has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "401", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/available-positions")
    public ResponseEntity<List<PositionDto>> getAllAvailablePositions(Authentication authentication) {
        return ResponseEntity.ok(positionService.getAvailablePositionsForInvitation(authentication));
    }

    @Operation(
            summary = "GET POSITIONS' WEIGHTS TO CREATE POSITION",
            description = "Accepts user's data, gets user's position weight and displays weights less than his",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position weights has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/available-weights")
    public ResponseEntity<List<Integer>> getPositionsWithWeightsLessThan(Authentication authentication) {
        return ResponseEntity.ok(positionService.getPositionsWithWeightsLessThan(authentication));
    }

    @GetMapping("/positions-by-weight-group")
    public ResponseEntity<PositionsWeightGroups> getPositionsByWeight(Authentication authentication) {
        return ResponseEntity.ok(positionService.getPositionsByWeight(authentication));
    }
}