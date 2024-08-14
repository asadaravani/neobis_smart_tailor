package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.PositionDto;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Operation(
            summary = "GET ALL POSITIONS TO INVITE EMPLOYEE",
            description = "Accepts user's data and and displays a list of positions, which weights are less than his position's weight",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position list has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "401", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/positions-to-invite-employee")
    public ResponseEntity<List<PositionDto>> getAllPositionsToInviteEmployee(Authentication authentication) {
        return ResponseEntity.ok(positionService.getPositionsToInviteEmployee(authentication));
    }

    @Operation(
            summary = "GET POSITION WEIGHTS LESS THAN AUTHENTICATED USER'S",
            description = "Accepts user's data, gets user's position weight and displays weights more than his",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position weights has been received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "User is not a member of any organization"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/positions-weights")
    public ResponseEntity<List<Integer>> getPositionsWithWeightsLessThan(Authentication authentication) {
        return ResponseEntity.ok(positionService.getPositionsWithWeightsLessThan(authentication));
    }
}