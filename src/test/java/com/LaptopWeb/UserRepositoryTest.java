package com.LaptopWeb;

import com.LaptopWeb.entity.Role;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        Role admin = testEntityManager.find(Role.class, "ADMIN");

        User user1 = User.builder()
                .username("vinhseo")
                .password("12345")
                .fullName("Mai Duc Vinh")
                .role(admin)
                .email("vinhseo@gmail.com")
                .phoneNumber("0123456789")
                .build();

        userRepository.save(user1);

    }
}
