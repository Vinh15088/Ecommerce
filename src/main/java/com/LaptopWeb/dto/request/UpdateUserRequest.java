package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @NotBlank(message = "FULLNAME_INVALID")
    String fullName;

    @Email(message = "EMAIL_INVALID")
    String email;

    String phoneNumber;
    LocalDate dob;
}
