package kg.neobis.smarttailor.service.impl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import kg.neobis.smarttailor.dtos.FirebaseNotificationRequest;
import kg.neobis.smarttailor.entity.DeviceToken;
import kg.neobis.smarttailor.service.DeviceTokenService;
import kg.neobis.smarttailor.service.FCMService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.ap.shaded.freemarker.debug.impl.DebuggerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FCMServiceImpl implements FCMService {

    DeviceTokenService deviceTokenService;

    @Override
    public void sendNotification(FirebaseNotificationRequest request) {
        List<DeviceToken> tokens = deviceTokenService.findAll();
        if(!tokens.isEmpty()){
            for (DeviceToken token : tokens) {
                sendNotificationToToken(token.getToken(), request.title(), request.body());
            }
        }
    }


    public void sendNotificationToToken(String token, String title, String body) {

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();



        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .build();


        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setBadge(1)
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.out.println("Error sending message to token " + token + ": " + e.getMessage());
        }
    }
}
