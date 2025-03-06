package com.LaptopWeb.dto.response.httpClient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OutboundUserFacebookResponse {
    String id;
    String email;
    String name;
    String firstName;
    String lastName;
    String picture;
}
