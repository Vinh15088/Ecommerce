package com.LaptopWeb.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brands")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 64, unique = true, nullable = false)
    String name;

    @Column(length = 512, unique = true, nullable = false)
    String logo;
}
