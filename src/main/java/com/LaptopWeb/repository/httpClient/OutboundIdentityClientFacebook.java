package com.LaptopWeb.repository.httpClient;

import com.LaptopWeb.dto.request.ExchangeTokenFacebookRequest;
import com.LaptopWeb.dto.response.httpClient.ExchangeTokenFacebookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "outbound-facebook-identity", url = "https://graph.facebook.com")
public interface OutboundIdentityClientFacebook {
    @PostMapping(value = "/v21.0/oauth/access_token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenFacebookResponse exchangeToken(@RequestBody ExchangeTokenFacebookRequest request);
}
