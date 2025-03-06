package com.LaptopWeb.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailDetails {
    String recipient;
    String msgBody;
    String subject;
    String attachment;
}
