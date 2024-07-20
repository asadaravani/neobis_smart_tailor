package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Equipment")
@RequestMapping(EndpointConstants.EQUIPMENT_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentController {

    EquipmentService service;

    @Operation(
            summary = "Get all equipments",
            description = "Using this endpoint it is possible to get all equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list"),
                    @ApiResponse(responseCode = "404", description = "Empty list"),
            }
    )
    @GetMapping("/get-all-equipments")
    public ResponseEntity<List<EquipmentListDto>> getAllEquipments() {
        return ResponseEntity.ok(service.getAllEquipments());
    }

    @Operation(
            summary = "Get detailed page of the equipment",
            description = "Using this endpoint it is possible to get detailed equipment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found"),
                    @ApiResponse(responseCode = "406", description = "Equipment is out of stock"),


            }
    )
    @GetMapping("/get-by-id/{equipmentId}")
    public ResponseEntity<EquipmentDto> getEquipmentById(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(service.getEquipmentById(equipmentId));
    }


    @Operation(
            summary = "Add equipment",
            description = "Whenever user wants to add a new equipment then he or she should to use this endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment successfully added"),
                    @ApiResponse(responseCode = "403", description = "Authentication required"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add-equipment")
    public ResponseEntity<String> addEquipment(@RequestPart("equipmentDto") String equipmentDto,
                                               @RequestPart("photos") List<MultipartFile> images,
                                               Authentication authentication) {
        return ResponseEntity.ok(service.addEquipment(equipmentDto, images, authentication));
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
        return ResponseEntity.ok(service.buyEquipment(equipmentId, authentication));
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
    public ResponseEntity<List<EquipmentListDto>> searchEquipments(@RequestParam(name = "query")String query, Authentication authentication) {
        return ResponseEntity.ok().body(service.searchEquipments(query, authentication));
    }

}