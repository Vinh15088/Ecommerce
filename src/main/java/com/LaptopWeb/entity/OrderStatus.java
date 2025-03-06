package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "status_order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    String description;

    public OrderStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
