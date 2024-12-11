package com.ccnta.app.security.configuration;

import com.ccnta.app.user.model.PrincipleUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {

        if (context.getTokenType().getValue().equals("access_token")) {
            Authentication principal = context.getPrincipal();
            Set<String> authorities = principal.getAuthorities()
                    .stream()
                    .map(authority -> "ROLE_" + authority.getAuthority().toUpperCase())
                    .collect(Collectors.toSet());

            PrincipleUser principleUser = (PrincipleUser) principal.getPrincipal();
            Map<String, String> user = new HashMap<>();
            user.put("first_name", principleUser.getUser().getFirstName());
            user.put("last_name", principleUser.getUser().getLastName());
            user.put("username", principleUser.getUser().getUsername());

            context.getClaims()
                    .claim("authorities", authorities)
                    .claim("user", user);
        }

    }
}