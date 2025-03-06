package com.LaptopWeb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "invalidated_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {

    @Id
    String id;

    Date expiryTime;
}
