package com.ccnta.app.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenSettingRequest {

    private String tokenType;
    private int accessTokenTimeToLive;
    private int refreshTokenTimeToLive;
    private int authorizationCodeTimeToLive;
    private boolean reuseRefreshToken;

}