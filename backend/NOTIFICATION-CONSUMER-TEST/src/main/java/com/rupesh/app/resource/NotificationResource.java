package com.rupesh.app.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class NotificationResource {

    @GetMapping("/notifications")
    public String notifications() {
        return "notifications";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
