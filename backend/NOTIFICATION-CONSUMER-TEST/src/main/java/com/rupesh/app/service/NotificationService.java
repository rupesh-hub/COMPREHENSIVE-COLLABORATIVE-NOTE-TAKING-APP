package com.rupesh.app.service;

import com.rupesh.app.model.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private int notificationCount = 0;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Send notification to a specific user
    public void sendNotification(String recipient) {
        notificationCount++;
        Notification notification = new Notification("Notification #" + notificationCount);
        messagingTemplate.convertAndSendToUser(recipient.equalsIgnoreCase("rupesh") ? "admin" : "rupesh", "/queue/notifications", notification);
    }

    // Broadcast notification to all users
    public void broadcastNotification() {
        notificationCount++;
        Notification notification = new Notification("Broadcast Notification #" + notificationCount);
        messagingTemplate.convertAndSend("/topic/broadcast", notification);
    }
}
