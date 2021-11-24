package com.piggymetrics.auth.domain;

import org.apache.commons.codec.binary.Base64;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.SerializationUtils;

@Document(collection = "refreshTokens")
public class RefreshToken {

    public static final String TOKEN_ID = "tokenId";

    @Id
    private String id;

    private String tokenId;
    private OAuth2RefreshToken token;
    private String authentication;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public OAuth2RefreshToken getToken() {
        return token;
    }

    public void setToken(OAuth2RefreshToken token) {
        this.token = token;
    }

    public OAuth2Authentication getAuthentication() {
        return (OAuth2Authentication) SerializationUtils.deserialize(Base64.decodeBase64(authentication));
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = Base64.encodeBase64String(SerializationUtils.serialize(authentication));
    }
}