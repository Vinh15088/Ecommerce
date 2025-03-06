package com.LaptopWeb.security;

import com.LaptopWeb.dto.request.IntrospectTokenRequest;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomerDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder;


    @Override
    public Jwt decode(String token) throws AppException {
        try {
            var response = authenticationService.introspectTokenResponse(
                    IntrospectTokenRequest.builder()
                            .token(token)
                            .build()
            );

            // ìf the token is invalid
            if(!response.isValid()) throw new JwtException(ErrorApp.TOKEN_INVALID.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        // if the nimbusJwtDecoder is noll, create SecretKeySpec with the secret key
        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");

            // configure nimbusJwtDecoder
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);

    }
}
