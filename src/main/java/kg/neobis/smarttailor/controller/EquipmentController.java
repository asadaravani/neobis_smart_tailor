package kg.neobis.smarttailor.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.common.EndpointConstants;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found"),
            }
    )
    @GetMapping("/get-all-equipments")
    public ResponseEntity<List<EquipmentListDto>> getAllEquipments(){
        return ResponseEntity.ok(equipmentService.getAllEquipments());
    }
}
