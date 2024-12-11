package com.ccnta.app.authentication;

import com.ccnta.app.security.model.ClientRegistrationRequest;
import com.ccnta.app.security.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthenticationResource {

    private final ClientService clientService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/client/register")
    public ResponseEntity<Map<String, String>> registerClient(@RequestBody ClientRegistrationRequest request) {
        clientService.save(ClientRegistrationRequest.toRegisterClient(request));
        Map<String, String> map = new HashMap<>();
        map.put("message","success");
        map.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(map);
    }

}