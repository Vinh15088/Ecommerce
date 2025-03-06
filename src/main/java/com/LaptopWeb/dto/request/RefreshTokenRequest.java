package com.LaptopWeb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {
    String token;
}
