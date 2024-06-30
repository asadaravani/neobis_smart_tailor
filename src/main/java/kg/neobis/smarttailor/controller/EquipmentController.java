package kg.neobis.smarttailor.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Equipment")
@RequestMapping("/equipment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentController {

    EquipmentService equipmentService;
    @Operation(
            summary = "Get all equipments",
            description = "Using this endpoint it is possible to get all equipments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment list"),
                    @ApiResponse(responseCode = "404", description = "Empty list"),
            }
    )
    @GetMapping("/get-all-equipments")
    public ResponseEntity<List<EquipmentListDto>> getAllEquipments(){
        return ResponseEntity.ok(equipmentService.getAllEquipments());
    }

    @Operation(
            summary = "Get detailed page of the equipment",
            description = "Using this endpoint it is possible to get detailed equipment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment"),
                    @ApiResponse(responseCode = "404", description = "Equipment not found"),

            }
    )
    @GetMapping("/get-by-id/{equipmentId}")
    public ResponseEntity<EquipmentDto> getEquipmentById(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(equipmentId));
    }


    @Operation(
            summary = "Add equipment",
            description = "Whenever user wants to add a new equipment then he or she should to use this endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Equipment successfully added"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @PostMapping("/add-equipment")
    public ResponseEntity<String> addEquipment(@RequestPart("equipmentDto") String equipmentDto,
                                            @RequestPart("photos") List<MultipartFile> images,
                                            Authentication authentication) throws FileUploadException {
        return ResponseEntity.ok(equipmentService.addEquipment(equipmentDto, images, authentication));
    }





}
