package com.rupesh.app.resource;

import com.rupesh.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationSenderResource {

    private final NotificationService notificationService;

    @GetMapping("/private")
    public ResponseEntity<String> send(Authentication authentication){
        notificationService.sendNotification(authentication.getName());

        return new ResponseEntity<>("Message send success!", HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<String> broadcast(){
        notificationService.broadcastNotification();
        return new ResponseEntity<>("Message broadcast success!", HttpStatus.OK);
    }

}
