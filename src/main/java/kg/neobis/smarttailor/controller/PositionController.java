package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Position")
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.POSITION_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {

    PositionService service;

    @Operation(
            summary = "CREATE POSITION",
            description = "The method accepts position's name and permissions to create the position in organization",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Position has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User has no permission to create position"),
                    @ApiResponse(responseCode = "409", description = "Position with specified name already exists "),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add-position")
    public ResponseEntity<String> addPosition(@RequestPart("position") String position, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addPosition(position, authentication));
    }
}