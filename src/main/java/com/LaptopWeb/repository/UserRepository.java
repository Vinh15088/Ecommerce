package com.LaptopWeb.repository;

import com.LaptopWeb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String username);


    @Query("SELECT u FROM User u WHERE u.username LIKE %?1% " +
            "OR u.fullName LIKE %?1% " +
            "OR u.email LIKE %?1% " +
            "OR u.phoneNumber LIKE %?1%")
    Page<User> findAllUser(String keyword, Pageable pageable);
}
