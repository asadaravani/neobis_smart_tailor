package kg.neobis.smarttailor.controller;

import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;
    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public String sendNotification( NotificationDto message) {
        return "Notification sent";
    }

}