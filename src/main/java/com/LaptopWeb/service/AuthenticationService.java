package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.*;
import com.LaptopWeb.dto.response.AuthenticationResponse;
import com.LaptopWeb.dto.response.httpClient.ExchangeTokenFacebookResponse;
import com.LaptopWeb.dto.response.httpClient.ExchangeTokenGoogleResponse;
import com.LaptopWeb.dto.response.IntrospectTokenResponse;
import com.LaptopWeb.entity.InvalidatedToken;
import com.LaptopWeb.entity.Role;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.enums.AuthProvider;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.repository.InvalidatedTokenRepository;
import com.LaptopWeb.repository.httpClient.OutboundIdentityClientFacebook;
import com.LaptopWeb.repository.httpClient.OutboundIdentityClientGoogle;
import com.LaptopWeb.repository.UserRepository;
import com.LaptopWeb.repository.httpClient.OutboundUserFacebookClient;
import com.LaptopWeb.repository.httpClient.OutboundUserGoogleClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    @Value("${outbound.identity.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${outbound.identity.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${outbound.identity.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${outbound.identity.facebook.client-id}")
    private String FACEBOOK_CLIENT_ID;

    @Value("${outbound.identity.facebook.client-secret}")
    private String FACEBOOK_CLIENT_SECRET;

    @Value("${outbound.identity.facebook.redirect-uri}")
    private String FACEBOOK_REDIRECT_URI;

    private String GRANT_TYPE = "authorization_code";

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final OutboundIdentityClientGoogle outboundIdentityClientGoogle;
    private final OutboundUserGoogleClient outboundUserGoogleClient;
    private final OutboundIdentityClientFacebook outboundIdentityClientFacebook;
    private final OutboundUserFacebookClient outboundUserFacebookClient;


    public AuthenticationResponse authenticationResponse(LoginRequest request) {
        User user = null;

        // check username or email for login
        if(request.getUsername() != null) {
            user = userService.getByUsername(request.getUsername());
        } else {
            user = userService.getByEmail(request.getEmail());
        }

        // check password for login
        if(user != null) {
            // compare provided password with the one stored in database
            boolean check = passwordEncoder.matches(request.getPassword(), user.getPassword());

            // if password is incorrect
            if(!check) throw  new AppException(ErrorApp.PASSWORD_INCORRECT);

            // generate jwt token
            String token = generateToken(user);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } throw new AppException(ErrorApp.USER_NOTFOUND);
    }

    public AuthenticationResponse outboundAuthenticateGoogle(String code) {
        ExchangeTokenGoogleRequest request = ExchangeTokenGoogleRequest.builder()
                .code(code)
                .clientId(GOOGLE_CLIENT_ID)
                .clientSecret(GOOGLE_CLIENT_SECRET)
                .redirectUri(GOOGLE_REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build();

        ExchangeTokenGoogleResponse response = outboundIdentityClientGoogle.exchangeToken(request);

        log.info("Exchange token response: {}", response);

        var userInfo = outboundUserGoogleClient.getUserInfo("json", response.getAccessToken());

        log.info("Exchange user info: {}", userInfo);

        String username = userInfo.getEmail().split("@")[0];

        User user = userRepository.findByUsername(username).filter(existingUser ->
                existingUser.getEmail().equals(userInfo.getEmail())).orElseGet(() ->
                userRepository.save(User.builder()
                                .username(username)
                                .email(userInfo.getEmail())
                                .fullName(
                                        userInfo.getFamilyName() != null ?
                                                userInfo.getFamilyName() + " " + userInfo.getGivenName() :
                                                userInfo.getGivenName())
                                .authProvider(AuthProvider.GOOGLE)
                                .role(Role.builder()
                                        .name("USER")
                                        .build())
                        .build()));

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse outboundAuthenticateFacebook(String code) {
        ExchangeTokenFacebookRequest request = ExchangeTokenFacebookRequest.builder()
                .code(code)
                .clientId(FACEBOOK_CLIENT_ID)
                .clientSecret(FACEBOOK_CLIENT_SECRET)
                .redirectUri(FACEBOOK_REDIRECT_URI)
                .build();

        ExchangeTokenFacebookResponse response = outboundIdentityClientFacebook.exchangeToken(request);

        log.info("Exchange token response: {}", response);

        var userInfo = outboundUserFacebookClient.getUserInfo("json", response.getAccessToken());

        log.info("Exchange user info: {}", userInfo);

        return AuthenticationResponse.builder()
                .token(response.getAccessToken())
                .build();
    }

    // method create token from user
    private String generateToken(User user) {
        Map<String, Object> dataUser = new HashMap<>();

        dataUser.put("id", user.getId());
        dataUser.put("username", user.getUsername());
        dataUser.put("email", user.getEmail());
        dataUser.put("fullName", user.getFullName());
        dataUser.put("phone", user.getPhoneNumber());

        // create jwt header with hs512 signing algorith
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // create jwt claims set containing user info and token metadata
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // username of the user
                .issuer("Laptop Store")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString()) // unique identifier for the token
                .claim("scope", user.getRole().getName()) // custom claim: user's role
                .claim("data", dataUser)
                .build();

        // convert claims set to a payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // create jws obj (header + payload)
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);


        try {
            // sign jwt with macsigner, signerkey
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Can not create token: " + e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        // create a verifier for the token
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        // parse token into a signedJwt obj
        SignedJWT signedJWT = SignedJWT.parse(token);

        // get the expiration time of the token from the claims
        Date expiryTime = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                        : signedJWT.getJWTClaimsSet().getExpirationTime();

        // verify signedJwt with jwsVerifier from signer_key
        var verified = signedJWT.verify(jwsVerifier);

        // check token is not verified or has expired
        if(!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorApp.TOKEN_INVALID);

        // check if token has already been invalidated in the repo
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorApp.TOKEN_INVALID);

        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        // verify provided token to ensure it is valid
        var signJWT = verifyToken(request.getToken(), true);

        // get jwt id and expiration time of old token
        var jit = signJWT.getJWTClaimsSet().getJWTID();
//        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
        var expiryTime = new Date(signJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        // store old token in the repo
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // extract username from the token claims
        String username = signJWT.getJWTClaimsSet().getSubject();

        User user = userService.getByUsername(username);

        // create a new token for user
        var tokenNew = generateToken(user);

        // return the new token in the AuthenticationResponse
        return AuthenticationResponse.builder()
                .token(tokenNew)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            // verify token
            var signToken = verifyToken(request.getToken(), true);

            // extract the jwt id
            String jwtId = signToken.getJWTClaimsSet().getJWTID();

            // calculate the expiry time of the invalidated token
            Date expiryTime = new Date(signToken.getJWTClaimsSet().getExpirationTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

            // create an invalidatedToken obj
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .expiryTime(expiryTime)
                    .id(jwtId)
                    .build();

            // save invalidatedToken
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    public IntrospectTokenResponse introspectTokenResponse(IntrospectTokenRequest request) throws ParseException, JOSEException {
        // get token from the request
        var token = request.getToken();
        boolean valid = true;

        try {
            // verify token
            verifyToken(token, false);
        } catch (AppException e) {
            valid = false;
        }

        return IntrospectTokenResponse.builder()
                .valid(valid)
                .build();
    }

}
