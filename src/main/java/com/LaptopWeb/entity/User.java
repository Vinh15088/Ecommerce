package com.LaptopWeb.entity;

import com.LaptopWeb.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 64, unique = true, nullable = false)
    String username;

    @Column(length = 64, nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    AuthProvider authProvider;

    @Column(name = "full_name", length = 64, unique = true, nullable = false)
    String fullName;

    @ManyToOne()
    @JoinColumn(name = "role_name")
    Role role;

    @Column(length = 64, unique = true, nullable = false)
    String email;

    @Column(length = 32, nullable = false)
    String phoneNumber;

    LocalDate dob;

}
