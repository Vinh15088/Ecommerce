package com.LaptopWeb;

import com.LaptopWeb.entity.Role;
import com.LaptopWeb.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {
    @Autowired
    RoleRepository roleRopository;

    @Test
    public void testAddRole() {
        Role admin = Role.builder()
                .name("ADMIN")
                .description("Admin role")
                .build();

        Role user = Role.builder()
                .name("USER")
                .description("User role")
                .build();

        roleRopository.save(admin);
        roleRopository.save(user);
    }
}
