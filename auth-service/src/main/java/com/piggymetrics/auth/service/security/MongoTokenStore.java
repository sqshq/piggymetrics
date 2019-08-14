package com.piggymetrics.auth.service.security;

import com.mongodb.client.result.UpdateResult;
import com.piggymetrics.auth.domain.AccessToken;
import com.piggymetrics.auth.domain.RefreshToken;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MongoTokenStore implements TokenStore {

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken accessToken) {
        return readAuthentication(accessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where(AccessToken.TOKEN_ID).is(extractTokenKey(token)));

        AccessToken accessToken = mongoTemplate.findOne(query, AccessToken.class);
        return accessToken != null ? accessToken.getAuthentication() : null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        String refreshToken = null;
        if (oAuth2AccessToken.getRefreshToken() != null) {
            refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
        }

        Query query = Query.query(Criteria.where(AccessToken.TOKEN_ID).is(extractTokenKey(oAuth2AccessToken.getValue())));
        Update update = Update.update("tokenId", extractTokenKey(oAuth2AccessToken.getValue()))
                .set("token", oAuth2AccessToken)
                .set("authenticationId", (authenticationKeyGenerator.extractKey(oAuth2Authentication)))
                .set("username", (oAuth2Authentication.isClientOnly() ? null : oAuth2Authentication.getName()))
                .set("clientID", oAuth2Authentication.getOAuth2Request().getClientId())
                .set("authentication", Base64.encodeBase64String(SerializationUtils.serialize(oAuth2Authentication)))
                .set("refreshTokenKey", extractTokenKey(refreshToken));
        UpdateResult updateResult = mongoTemplate.upsert(query, update, AccessToken.class);

    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where(AccessToken.TOKEN_ID).is(extractTokenKey(tokenValue)));

        AccessToken accessToken = mongoTemplate.findOne(query, AccessToken.class);
        return accessToken != null ? accessToken.getToken() : null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(AccessToken.TOKEN_ID).is(extractTokenKey(oAuth2AccessToken.getValue())));
        mongoTemplate.remove(query, AccessToken.class);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        RefreshToken token = new RefreshToken();
        token.setTokenId(extractTokenKey(refreshToken.getValue()));
        token.setToken(refreshToken);
        token.setAuthentication(authentication);
        mongoTemplate.save(token);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where(RefreshToken.TOKEN_ID).is(extractTokenKey(tokenValue)));

        RefreshToken refreshToken = mongoTemplate.findOne(query, RefreshToken.class);
        return refreshToken != null ? refreshToken.getToken() : null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(RefreshToken.TOKEN_ID).is(extractTokenKey(oAuth2RefreshToken.getValue())));

        RefreshToken refreshToken = mongoTemplate.findOne(query, RefreshToken.class);
        return refreshToken != null ? refreshToken.getAuthentication() : null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(RefreshToken.TOKEN_ID).is(extractTokenKey(refreshToken.getValue())));
        mongoTemplate.remove(query, RefreshToken.class);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(AccessToken.REFRESH_TOKEN).is(extractTokenKey(refreshToken.getValue())));
        mongoTemplate.remove(query, AccessToken.class);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken oAuth2AccessToken = null;
        String authenticationId = authenticationKeyGenerator.extractKey(authentication);

        Query query = new Query();
        query.addCriteria(Criteria.where(AccessToken.AUTHENTICATION_ID).is(authenticationId));

        AccessToken accessToken = mongoTemplate.findOne(query, AccessToken.class);
        if (accessToken != null) {
            oAuth2AccessToken = accessToken.getToken();
            if (oAuth2AccessToken != null && !authenticationId.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(oAuth2AccessToken)))) {
                this.removeAccessToken(oAuth2AccessToken);
                this.storeAccessToken(oAuth2AccessToken, authentication);
            }
        }
        return oAuth2AccessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username) {
        return findTokensByCriteria(
                Criteria.where(AccessToken.CLIENT_ID).is(clientId)
                        .and(AccessToken.USER_NAME).is(username));
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return findTokensByCriteria(Criteria.where(AccessToken.CLIENT_ID).is(clientId));
    }

    private Collection<OAuth2AccessToken> findTokensByCriteria(Criteria criteria) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(criteria);
        List<AccessToken> accessTokens = mongoTemplate.find(query, AccessToken.class);
        for (AccessToken accessToken : accessTokens) {
            tokens.add(accessToken.getToken());
        }
        return tokens;
    }

    private String extractTokenKey(String value) {
        if (value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] bytes = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new BigInteger(1, bytes));
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}