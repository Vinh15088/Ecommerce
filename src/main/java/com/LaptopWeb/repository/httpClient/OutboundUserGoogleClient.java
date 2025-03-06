package com.LaptopWeb.repository.httpClient;

import com.LaptopWeb.dto.response.httpClient.OutboundUserGoogleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-google-user-client", url = "https://www.googleapis.com")
public interface OutboundUserGoogleClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserGoogleResponse getUserInfo(@RequestParam("alt") String alt,
                                     @RequestParam("access_token") String accessToken);
}
