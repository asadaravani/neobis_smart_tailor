package kg.neobis.smarttailor.controller;

import kg.neobis.smarttailor.dtos.NotificationDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NotificationController {
    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public String sendNotification(NotificationDto message) {
        return "Notification sent";
    }
}