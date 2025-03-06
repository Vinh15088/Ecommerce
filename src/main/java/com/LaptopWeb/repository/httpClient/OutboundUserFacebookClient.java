package com.LaptopWeb.repository.httpClient;

import com.LaptopWeb.dto.response.httpClient.OutboundUserFacebookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-facebook-user-client", url = "https://graph.facebook.com")
public interface OutboundUserFacebookClient {
    @GetMapping(value = "/me")
    OutboundUserFacebookResponse getUserInfo(@RequestParam("fields") String fields,
                                             @RequestParam("access_token") String accessToken);
}
