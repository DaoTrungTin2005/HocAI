package com.hocai.config;

import com.hocai.entity.Role;
import com.hocai.entity.User;
import com.hocai.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Tạo tài khoản mẫu ở lần chạy đầu tiên (mật khẩu: 123456). */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }
            repo.save(newUser("teacher", "Cô giáo Demo", Role.TEACHER, null, encoder));
            repo.save(newUser("hs1", "Học sinh 1", Role.STUDENT, "EXPERIMENT", encoder));
            repo.save(newUser("hs2", "Học sinh 2", Role.STUDENT, "CONTROL", encoder));
        };
    }

    private User newUser(String username, String fullName, Role role, String group, PasswordEncoder encoder) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode("123456"));
        u.setFullName(fullName);
        u.setRole(role);
        u.setStudyGroup(group);
        return u;
    }
}
