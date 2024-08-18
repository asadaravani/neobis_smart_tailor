package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.AuthorEquipmentDetailedDto;
import kg.neobis.smarttailor.dtos.EquipmentDetailed;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Equipment")
@RequestMapping(EndpointConstants.EQUIPMENT_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentController {

    EquipmentService equipmentService;

    @Operation(
            summary = "ADD EQUIPMENT",
            description = "Accepts equipment's data and images to create the equipment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Equipment has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present | Entered data has not been validated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/add-equipment")
    public ResponseEntity<String> addEquipment(@RequestPart("equipment") String equipmentDto,
                                               @RequestPart("images") List<MultipartFile> images,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.addEquipment(equipmentDto, images, authentication));
    }

    @Operation(
            summary = "BUY EQUIPMENT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment has been bought"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type | Users can't buy their own equipments"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified email"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/buy-equipment/{equipmentId}")
    public ResponseEntity<String> buyEquipment(@PathVariable Long equipmentId,
                                               Authentication authentication) {
        return ResponseEntity.ok(equipmentService.buyEquipment(equipmentId, authentication));
    }

    @Operation(
            summary = "DELETE EQUIPMENT",
            description = "Deletes equipment by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment has been deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Only authors can delete their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @DeleteMapping("/delete-equipment/{equipmentId}")
    public ResponseEntity<String> deleteEquipment(@PathVariable Long equipmentId,
                                                  Authentication authentication) throws IOException {
        return ResponseEntity.ok(equipmentService.deleteEquipment(equipmentId, authentication));
    }

    @Operation(
            summary = "GET ALL EQUIPMENTS",
            description = "Returns list of visible equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-all-equipments")
    public ResponseEntity<AdvertisementPageDto> getAllVisibleEquipments(@RequestParam int pageNumber,
                                                                        @RequestParam int pageSize) {
        return ResponseEntity.ok(equipmentService.getAllVisibleEquipments(pageNumber, pageSize));
    }

    @Operation(
            summary = "GET EQUIPMENT DETAILED",
            description = "Accepts equipment's id, and returns equipment's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment's information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-equipment-detailed/{equipmentId}")
    public ResponseEntity<EquipmentDetailed> getEquipmentDetailed(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.getEquipmentDetailed(equipmentId));
    }

    @Operation(
            summary = "GET EQUIPMENT DETAILED FOR AUTHOR",
            description = "Accepts equipment's id, and returns equipment's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "User is not an author of this equipment"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/get-equipment-detailed-for-author/{equipmentID}")
    public ResponseEntity<AuthorEquipmentDetailedDto> getEquipmentDetailedForAuthor(@PathVariable Long equipmentID,
                                                                                    Authentication authentication) {
        return ResponseEntity.ok(equipmentService.getEquipmentDetailedForAuthor(equipmentID, authentication));
    }

    @Operation(
            summary = "USER'S EQUIPMENT",
            description = "Returns list of user's equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/my-equipments")
    public ResponseEntity<AdvertisementPageDto> getUserEquipments(@RequestParam int pageNumber,
                                                                  @RequestParam int pageSize,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(equipmentService.getUserEquipments(pageNumber, pageSize, authentication));
    }

    @Operation(
            summary = "HIDE EQUIPMENT",
            description = "Accepts equipment's id and then makes ad invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Equipment is already hidden | Only authors can hide their advertisements"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/hide/{equipmentId}")
    public ResponseEntity<String> hideEquipment(@PathVariable Long equipmentId, Authentication authentication) {
        return ResponseEntity.ok(equipmentService.hideEquipment(equipmentId, authentication));
    }

}