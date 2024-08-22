package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.FirebaseNotificationRequest;

import java.util.concurrent.ExecutionException;

public interface FCMService {

    void sendNotification(FirebaseNotificationRequest request);
}
