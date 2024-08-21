package kg.neobis.smarttailor.controller;

import kg.neobis.smarttailor.dtos.FirebaseNotificationRequest;
import kg.neobis.smarttailor.dtos.NotificationDto;
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
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    FCMService fcmService;

    EmailService emailService;
    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public String sendNotification( NotificationDto message) {
        return "Notification sent";
    }

    @PostMapping("/notification/push")
    public ResponseEntity<String> sendTokenNotification(@RequestBody FirebaseNotificationRequest request) {
        fcmService.sendMessageToToken(request);
        System.out.println("Notification sent!");
        return new ResponseEntity<>( "Notification has been sent.", HttpStatus.OK);
    }

}