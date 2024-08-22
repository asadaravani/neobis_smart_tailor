package kg.neobis.smarttailor.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.neobis.smarttailor.constants.EndpointConstants;
import kg.neobis.smarttailor.dtos.DeviceTokenRequestDto;
import kg.neobis.smarttailor.dtos.FirebaseNotificationRequest;
import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.service.DeviceTokenService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.FCMService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Notification")
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.NOTIFICATION_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    DeviceTokenService deviceTokenService;
    FCMService fcmService;
    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public String sendNotification( NotificationDto message) {
        return "Notification sent";
    }

    @Hidden
    @PostMapping("/notification/push")
    public ResponseEntity<String> sendTokenNotification(@RequestBody FirebaseNotificationRequest request) {
        fcmService.sendNotification(request);
        System.out.println("Notification sent!");
        return new ResponseEntity<>( "Notification has been sent.", HttpStatus.OK);
    }


    @Operation(
            summary = "REGISTER DEVICE TOKEN",
            description = "Accepts device token and saves it into the database",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Saved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
            }
    )

    @PostMapping("/register-device-token")
    public ResponseEntity<?> registerToken(@RequestBody DeviceTokenRequestDto request) {
        String token = request.token();
        if (token != null && !token.isEmpty()) {
            deviceTokenService.saveToken(token);
            return ResponseEntity.ok("Token registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

}