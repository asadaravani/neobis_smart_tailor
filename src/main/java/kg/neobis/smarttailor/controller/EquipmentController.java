package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.AdvertisementPageDto;
import kg.neobis.smarttailor.dtos.ads.detailed.EquipmentDetailed;
import kg.neobis.smarttailor.dtos.ads.list.EquipmentListDto;
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
            description = "The method accepts equipment data and images to create the equipment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Equipment has been created"),
                    @ApiResponse(responseCode = "400", description = "Required parameter(s) is not present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add-equipment")
    public ResponseEntity<String> addEquipment(@RequestPart("equipment") String equipmentDto,
                                               @RequestPart("images") List<MultipartFile> images,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.addEquipment(equipmentDto, images, authentication));
    }

    @Operation(
            summary = "DELETE EQUIPMENT",
            description = "To delete equipment, the method accepts its id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment has been deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/delete-equipment/{equipmentId}")
    public ResponseEntity<String> deleteEquipment(@PathVariable Long equipmentId) throws IOException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(equipmentService.deleteEquipment(equipmentId));
    }

    @Operation(
            summary = "GET ALL EQUIPMENTS",
            description = "The method returns list of equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipments not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get-all-equipments")
    public ResponseEntity<AdvertisementPageDto> getAllEquipments(@RequestParam int pageNumber,
                                                                 @RequestParam int pageSize) {
        return ResponseEntity.ok(equipmentService.getAllEquipments(pageNumber, pageSize));
    }

    @Operation(
            summary = "GET EQUIPMENT DETAILED",
            description = "The method accepts equipment's id, and then sends equipment's detailed information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment information received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/get-equipment-detailed/{equipmentId}")
    public ResponseEntity<EquipmentDetailed> getEquipmentDetailed(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(equipmentId));
    }

    @Operation(
            summary = "HIDE EQUIPMENT",
            description = "The method accepts equipment's id and then makes order invisible in marketplace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment is now invisible in marketplace"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Invalid jwt or authorization type"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found with specified id"),
                    @ApiResponse(responseCode = "409", description = "Equipment is already hidden | Only authors can hide their equipments"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/hide/{equipmentId}")
    public ResponseEntity<String> hideOrder(@PathVariable Long equipmentId, Authentication authentication) {
        return ResponseEntity.ok(equipmentService.hideEquipment(equipmentId, authentication));
    }

    @Operation(
            summary = "Buy an equipment",
            description = "This endpoint is designed to buy an equipment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment has been bought successfully"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/buy-equipment/{equipmentId}")
    public ResponseEntity<String> buyEquipment(@PathVariable Long equipmentId,
                                               Authentication authentication) {
        return ResponseEntity.ok(equipmentService.buyEquipment(equipmentId, authentication));
    }

    @Operation(
            summary = "Search equipments",
            description = "This endpoint is designed to search equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns list of equipments"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/search-equipment")
    public ResponseEntity<List<EquipmentListDto>> searchEquipments(@RequestParam(name = "query") String query, Authentication authentication) {
        return ResponseEntity.ok().body(equipmentService.searchEquipments(query, authentication));
    }
}