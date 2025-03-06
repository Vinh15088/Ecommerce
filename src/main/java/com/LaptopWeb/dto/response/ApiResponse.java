package com.LaptopWeb.dto.response;

import com.LaptopWeb.utils.PageInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse <T> {
    boolean success;
    Integer code;
    String message;
    T content;
    PageInfo pageInfo;
}
