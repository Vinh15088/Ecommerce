package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
    @Id
    String name;

    String description;
}
