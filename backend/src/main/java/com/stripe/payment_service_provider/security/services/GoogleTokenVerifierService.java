package com.stripe.payment_service_provider.security.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.stripe.payment_service_provider.settings.exceptions.auth.InvalidGoogleJwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;
        } catch (GeneralSecurityException | IllegalArgumentException | IOException e) {
           throw new InvalidGoogleJwtToken("Invalid Google ID token");
        } catch (Exception e) {
            throw new InvalidGoogleJwtToken("Something went wrong during google id token verification", e);
        }
    }
}
