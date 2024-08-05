package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.NotificationDto;

public interface NotificationService {
    void sendNotification(NotificationDto message);
}
