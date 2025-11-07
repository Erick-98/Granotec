package com.granotec.inventory_api.auth.service;

import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.user.User;
import com.granotec.inventory_api.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private com.granotec.inventory_api.auth.repository.TokenRepository tokenRepository;

    private Role testRole;
    private boolean roleCreated = false;
    private String testEmail;

    @BeforeEach
    public void setup() {
        // usar email único por ejecución para evitar collisiones con pruebas anteriores
        testEmail = "test-integration-" + UUID.randomUUID() + "@example.com";

        Optional<Role> existing = roleRepository.findByName("TEST_ROLE");
        if (existing.isPresent()) {
            testRole = existing.get();
            roleCreated = false;
        } else {
            testRole = Role.builder().name("TEST_ROLE").build();
            testRole = roleRepository.save(testRole);
            roleCreated = true;
        }

        // Crear usuario de prueba (se ejecuta y comitea en su propia tx)
        User user = User.builder()
                .name("Integration Test")
                .email(testEmail)
                .password("dummy")
                .role(testRole)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    public void cleanup() {
        // Eliminar tokens asociados (si existen)
        userRepository.findByEmail(testEmail).ifPresent(u -> {
            tokenRepository.findAllValidTokenByUser(u.getId()).forEach(tokenRepository::delete);
            userRepository.deleteById(u.getId());
        });

        // Eliminar rol si fue creado por la prueba
        if (roleCreated && testRole != null && testRole.getId() != null) {
            roleRepository.deleteById(testRole.getId());
        }
    }

    @Test
    public void testFailedAttemptsAndLocking() {
        final User user = userRepository.findByEmail(testEmail).orElseThrow();

        // Al crear el usuario, failedAttempts debe ser 0
        assertThat(user.getFailedAttempts()).isNotNull().isEqualTo(0);

        // Primer intento fallido
        authService.handleFailedLogin(user.getId());
        User after1 = userRepository.findById(user.getId()).orElseThrow();
        assertThat(after1.getFailedAttempts()).isEqualTo(1);
        assertThat(after1.getLockTime()).isNull();

        // Segundo intento fallido
        authService.handleFailedLogin(user.getId());
        User after2 = userRepository.findById(user.getId()).orElseThrow();
        assertThat(after2.getFailedAttempts()).isEqualTo(2);
        assertThat(after2.getLockTime()).isNull();

        // Tercer intento: debe bloquear y resetear failedAttempts a 0
        authService.handleFailedLogin(user.getId());
        User after3 = userRepository.findById(user.getId()).orElseThrow();
        assertThat(after3.getFailedAttempts()).isEqualTo(0);
        assertThat(after3.getLockTime()).isNotNull();
        assertThat(after3.getLockTime()).isAfter(LocalDateTime.now().minusSeconds(5));
    }
}
