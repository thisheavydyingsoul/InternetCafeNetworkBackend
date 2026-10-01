package com.internetcafe.security.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.internetcafe.config.GoogleOAuthProperties;
import com.internetcafe.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class GoogleIdTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenVerifierService(GoogleOAuthProperties properties)
    {
        String clientId = properties.getClientId();
        if (clientId == null || clientId.isBlank())
        {
            throw new IllegalStateException("GOOGLE_CLIENT_ID is not configured");
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(properties.getClientId()))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new UnauthorizedException("Invalid Google ID token", "GOOGLE_TOKEN_INVALID");
            }
            return idToken.getPayload();
        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Google ID token verification failed: {}", ex.getMessage());
            throw new UnauthorizedException("Google token verification failed", "GOOGLE_TOKEN_INVALID");
        }
    }
}
