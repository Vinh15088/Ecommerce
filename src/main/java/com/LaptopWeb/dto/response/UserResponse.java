package com.LaptopWeb.dto.response;

import com.LaptopWeb.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Integer id;
    String username;
    String fullName;
    String email;
    String phoneNumber;
    LocalDate dob;
}
