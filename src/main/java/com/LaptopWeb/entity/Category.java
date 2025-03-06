package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 128, unique = true, nullable = false)
    String name;

    @Column(length = 4096)
    String description;

    boolean enabled;

    @OneToMany(mappedBy = "parent")
    Set<Category> children;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    Category parent;
}
